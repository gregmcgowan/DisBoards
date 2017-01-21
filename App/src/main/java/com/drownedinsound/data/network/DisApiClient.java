package com.drownedinsound.data.network;

import com.drownedinsound.data.generatered.BoardPost;
import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.generatered.BoardPostSummary;
import com.drownedinsound.data.parser.streaming.DisWebPageParser;
import com.drownedinsound.data.parser.streaming.LoginException;
import com.drownedinsound.utils.StringUtils;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import timber.log.Timber;

/**
 * A service that will handle all the requests to the website
 *
 * @author Greg
 */
@Singleton
public class DisApiClient implements DisBoardsApi {

    public enum RequestMethod {
        GET(false),
        POST(true),
        PUT(true),
        DELETE(false);

        public final boolean hasRequestBody;

        RequestMethod(boolean hasRequestBody) {
            this.hasRequestBody = hasRequestBody;
        }
    }

    private OkHttpClient httpClient;;

    private CopyOnWriteArrayList<Object> inProgressRequests;

    private String baseUrl;

    private DisWebPageParser disWebPageParser;

    private NetworkUtil networkUtil;

    @Inject
    public DisApiClient(OkHttpClient httpClient,
            NetworkUtil networkUtil,
            DisWebPageParser disWebPageParser) {

        this.httpClient = httpClient;
        this.inProgressRequests = new CopyOnWriteArrayList<>();
        this.disWebPageParser = disWebPageParser;
        this.networkUtil = networkUtil;
        this.baseUrl = UrlConstants.BASE_URL;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public Observable<BoardPost> getBoardPost(final @BoardPostList.BoardPostListType String boardListType,final String boardPostId) {
        String url = UrlConstants.getBoardPostUrl(baseUrl, boardListType, boardPostId);
        return makeRequest(RequestMethod.GET,url,url)
                .flatMap(new Func1<Response, Observable<BoardPost>>() {
            @Override
            public Observable<BoardPost> call(Response response) {
                return parseBoardPost(boardListType,response);
            }
        });
    }

    public Observable<BoardPost> parseBoardPost(final @BoardPostList.BoardPostListType String boardListType, final Response response) {
        return Observable.create(new Observable.OnSubscribe<BoardPost>() {
            @Override
            public void call(Subscriber<? super BoardPost> subscriber) {
                try {
                    InputStream inputStream = getInputStreamFromResponse(response);
                    BoardPost boardPost
                            = disWebPageParser
                            .parseBoardPost(boardListType, inputStream);
                    inputStream.close();
                    subscriber.onNext(boardPost);
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }


    @Override
    public Observable<List<BoardPostSummary>> getBoardPostSummaryList(final @BoardPostList.BoardPostListType String boardListType,
            String boardPostUrl, int pageNumber) {

        final boolean append = pageNumber > 1;
        if (append) {
            boardPostUrl += "/page/" + pageNumber;
        }
        return makeRequest(RequestMethod.GET,boardPostUrl,boardPostUrl).flatMap(
                new Func1<Response, Observable<List<BoardPostSummary>>>() {
                    @Override
                    public Observable<List<BoardPostSummary>> call(Response response) {
                        try {
                            List<BoardPostSummary> boardPostList
                                    = disWebPageParser.parseBoardPostSummaryList(
                                    boardListType,
                                    getInputStreamFromResponse(response));
                            return Observable.just(boardPostList);
                        } catch (IOException e) {
                            return Observable.error(e);
                        }
                    }
                });
    }

    private Observable<Response> makeRequest(RequestMethod requestMethod, String url, Object tag) {
        return makeRequest(requestMethod, url, null, null, tag);
    }

    private Observable<Response> makeRequest(RequestMethod requestMethod, String url,
            RequestBody requestBody,
            Object tag) {
        return makeRequest(requestMethod, url, requestBody, null, tag);
    }

    private Observable<Response> makeRequest(RequestMethod requestMethod, String url,
            RequestBody requestBody,
            Headers.Builder extraHeaders, Object tag) {

        Headers.Builder headerBuilder = addMandatoryHeaders(extraHeaders);

        Request.Builder builder = new Request.Builder().
                headers(headerBuilder.build());

        if (RequestMethod.GET.equals(requestMethod)) {
            builder = builder.get();
        } else if (RequestMethod.POST.equals(requestMethod)) {
            builder = builder.post(requestBody);
        } else if (RequestMethod.PUT.equals(requestMethod)) {
            builder = builder.put(requestBody);
        } else if (RequestMethod.DELETE.equals(requestMethod)) {
            builder = builder.delete();
        } else {
            throw new IllegalArgumentException("Invalid request type");
        }

        return makeRequest(builder, url, tag);
    }

    private Observable<Response> makeRequest(final Request.Builder requestBuilder,
            final String url,
            final Object tag) {
        return Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(Subscriber<? super Response> subscriber) {
                if(networkUtil.isConnected()) {
                    inProgressRequests.add(tag);
                    Request request = requestBuilder.url(url).build();
                    try {
                        Response response = httpClient.newCall(request).execute();
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(response);
                            subscriber.onCompleted();
                        }
                    } catch (IOException e) {
                        subscriber.onError(e);
                    }
                } else {
                    subscriber.onError(new NoInternetConnectionException());
                }
            }
        }).timeout(10, TimeUnit.SECONDS);
    }

    protected Headers.Builder addMandatoryHeaders(Headers.Builder headers) {
        if (headers == null) {
            headers = new Headers.Builder();
        }

        headers.add("Cache-Control", "max-age=0");
        headers.add("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.97 Safari/537.11");
        headers
                .add("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        headers.add("Accept-Encoding", "gzip,deflate,sdch");
        headers.add("Accept-Language", "en-US,en;q=0.8,en-GB;q=0.6");
        headers.add("Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.3");
        return headers;
    }

    private InputStream getInputStreamFromResponse(Response response) throws IOException{
        String encodingHeader = response.header("Content-Encoding");
        boolean gzipped = encodingHeader != null && encodingHeader.contains("gzip");
        if (gzipped) {
            return new GZIPInputStream(response.body().byteStream());
        } else {
            return response.body().byteStream();
        }
    }

    @Override
    public boolean requestInProgress(Object tag) {
        return inProgressRequests.contains(tag);
    }


}

