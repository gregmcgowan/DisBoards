package com.drownedinsound;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;

/**
 * Created by gmcgowan on 14/01/2015.
 */
public class DisBoardsDaoGenerator {

    private Schema schema;

    private Entity boardPostListEntity;
    private Entity boardPostEntity;
    private Entity boardPostCommentEntity;

    public void generateModel() throws Exception {
        schema = new Schema(1,"com.drownedinsound.data.generatered");
        schema.enableKeepSectionsByDefault();

        addBoardPostList();

        new DaoGenerator().generateAll(schema,"App/src/main/java/");
    }

    private void addBoardPostList() {
        boardPostListEntity = schema.addEntity("BoardPostList");

        boardPostListEntity.addStringProperty("boardListTypeID").notNull().primaryKey();
        boardPostListEntity.addStringProperty("displayName").notNull();
        boardPostListEntity.addStringProperty("url").notNull();
        boardPostListEntity.addLongProperty("lastFetchedMs").notNull();
        boardPostListEntity.addIntProperty("sectionId").notNull();
        boardPostListEntity.addIntProperty("pageIndex").notNull();

        boardPostEntity = schema.addEntity("BoardPost");


        boardPostEntity.addStringProperty("boardPostID").primaryKey().getProperty();
        boardPostEntity.addStringProperty("title");
        boardPostEntity.addStringProperty("summary");
        boardPostEntity.addStringProperty("content");
        boardPostEntity.addStringProperty("authorUsername");
        boardPostEntity.addStringProperty("dateOfPost");
        boardPostEntity.addIntProperty("numberOfReplies");
        boardPostEntity.addLongProperty("lastViewedTime").notNull();
        boardPostEntity.addLongProperty("createdTime").notNull();
        boardPostEntity.addLongProperty("lastUpdatedTime").notNull();
        boardPostEntity.addStringProperty("latestCommentID");
        boardPostEntity.addIntProperty("numberOfTimesRead");
        boardPostEntity.addBooleanProperty("isFavourite").notNull();
        boardPostEntity.addBooleanProperty("isSticky").notNull();

        Property boardListTypeIDFK = boardPostEntity.addStringProperty("boardListTypeID").getProperty();
        boardPostEntity.addToOne(boardPostListEntity, boardListTypeIDFK, "boardPostList");

        boardPostListEntity.addToMany(boardPostEntity, boardListTypeIDFK, "posts");


        boardPostCommentEntity = schema.addEntity("BoardPostComment");
        boardPostCommentEntity.addStringProperty("commentID").primaryKey();
        boardPostCommentEntity.addStringProperty("title");
        boardPostCommentEntity.addStringProperty("content");
        boardPostCommentEntity.addStringProperty("authorUsername");
        boardPostCommentEntity.addStringProperty("replyToUsername");
        boardPostCommentEntity.addStringProperty("usersWhoHaveThissed");
        boardPostCommentEntity.addStringProperty("dateAndTime");
        boardPostCommentEntity.addIntProperty("commentLevel");

        Property commentEntityBoardPostId = boardPostCommentEntity.addStringProperty("boardPostID")
                .getProperty();
        boardPostCommentEntity.addToOne(boardPostEntity, commentEntityBoardPostId, "boardPost");

        boardPostEntity.addToMany(boardPostCommentEntity,commentEntityBoardPostId,"unorderedComments");



    }


}
