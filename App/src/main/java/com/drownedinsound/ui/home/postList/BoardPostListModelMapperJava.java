package com.drownedinsound.ui.home.postList;

import com.drownedinsound.BoardPostSummaryModel;
import com.drownedinsound.data.generatered.BoardPostSummary;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;


public class BoardPostListModelMapperJava {

    @Inject
    BoardPostListModelMapperJava() {
    }

    public
    @NonNull
    List<BoardPostSummaryModel> map(@NonNull List<BoardPostSummary> summaryList) {
        List<BoardPostSummaryModel> models = new ArrayList<>();
        int index = 0;
        for (BoardPostSummary boardPostSummary : summaryList) {
            models.add(map(boardPostSummary, index++));
        }
        return Collections.unmodifiableList(models);
    }

    private @NonNull BoardPostSummaryModel map(BoardPostSummary boardPostSummary, int index) {
        final int numberOfReplies = boardPostSummary.getNumberOfReplies();
        final String numberOfRepliesText;
        if (numberOfReplies > 0) {
            if (numberOfReplies > 1) {
                numberOfRepliesText = String.format("%d replies ", numberOfReplies);
            } else {
                numberOfRepliesText = "1 reply";
            }

        } else {
            numberOfRepliesText = "No replies";
        }
        boolean showLastUpdated = boardPostSummary.getLastViewedTime() > 0
                && boardPostSummary.getLastViewedTime() >= boardPostSummary.getLastUpdatedTime();
        String author = String.format("by %s", boardPostSummary.getAuthorUsername());

        return new BoardPostSummaryModel(index, boardPostSummary.getTitle(),
                author,
                numberOfRepliesText,
                boardPostSummary.getLastUpdatedInReadableString(),
                showLastUpdated
                , boardPostSummary.getIsSticky());
    }


}
