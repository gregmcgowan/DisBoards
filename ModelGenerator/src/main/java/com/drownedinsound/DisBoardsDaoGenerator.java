package com.drownedinsound;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
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

        addBoardPostEntities();

        new DaoGenerator().generateAll(schema,"App/src/main/java/");
    }

    private void addBoardPostEntities() {
        //I don't define the relationships between entities e.g 1 - many etc as these just
        //add in methods to the model classes that break the unit tests - they access the dao objects
        // which would need to be setup using roboeltric or such like. They are more
        //conveince methods anyway and the relationships do not get automatically updated
        //so we are not really losing that much by leaving them out
        boardPostListEntity = schema.addEntity("BoardPostList");
        boardPostListEntity.addStringProperty("boardListTypeID").notNull().primaryKey();
        boardPostListEntity.addStringProperty("displayName").notNull();
        boardPostListEntity.addStringProperty("url").notNull();
        boardPostListEntity.addLongProperty("lastFetchedMs").notNull();
        boardPostListEntity.addIntProperty("sectionId").notNull();
        boardPostListEntity.addIntProperty("pageIndex").notNull();

        boardPostEntity = schema.addEntity("BoardPost");
        boardPostEntity.addStringProperty("boardPostID").primaryKey();
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
        boardPostEntity.addStringProperty("boardListTypeID").getProperty();

        boardPostCommentEntity = schema.addEntity("BoardPostComment");
        boardPostCommentEntity.addStringProperty("commentID").primaryKey();
        boardPostCommentEntity.addStringProperty("title");
        boardPostCommentEntity.addStringProperty("content");
        boardPostCommentEntity.addStringProperty("authorUsername");
        boardPostCommentEntity.addStringProperty("replyToUsername");
        boardPostCommentEntity.addStringProperty("usersWhoHaveThissed");
        boardPostCommentEntity.addStringProperty("dateAndTime");
        boardPostCommentEntity.addIntProperty("commentLevel").notNull();
        boardPostCommentEntity.addStringProperty("boardPostID");



    }


}
