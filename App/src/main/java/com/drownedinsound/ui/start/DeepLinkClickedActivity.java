package com.drownedinsound.ui.start;

import com.drownedinsound.data.generatered.BoardPostList;
import com.drownedinsound.data.network.UrlConstants;
import com.drownedinsound.ui.post.BoardPostActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import timber.log.Timber;

/**
 * Created by gregmcgowan on 14/12/14.
 */
public class DeepLinkClickedActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        Uri data = intent.getData();
        String path = data.getPath();
        String url = data.toString();

        Timber.d("Path before " + path);
        path = path.replace("/community/boards/", "");

        Timber.d("Data Path  after " + path);

        if (path.contains("/")) {
            String[] boardAndPostID = path.split("/");
            String board = boardAndPostID[0];
            String postId = boardAndPostID[1];
            if (postId.contains("#last")) {
                postId = postId.replace("#last", "");
            } else if (postId.contains("#")) {
                postId = postId.replace("#", "");
            }

            @BoardPostList.BoardPostListType String boardListType = UrlConstants.getBoardType(url);
            Timber.d("Board " + board + " postID " + postId + " boardType " + boardListType);
            startActivity(BoardPostActivity.getIntent(this, postId, boardListType));
        } else {

        }

        finish();
        //TODO
//        startActivity(BoardPostActivity
//                .getIntent(getActivity(), postUrl, postId, boardType));
    }

}
