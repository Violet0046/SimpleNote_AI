package com.simplenote.backend.interaction;

import org.springframework.lang.NonNull;

import java.util.Objects;

public final class InteractionRedisKeys {
    public static final String POST_LIKE_DIRTY_KEY = "post:likes:dirty";
    public static final String POST_LIKE_PROCESSING_KEY = "post:likes:processing";
    public static final String USER_FOLLOWING_DIRTY_KEY = "user:following:dirty";
    public static final String USER_FOLLOWING_PROCESSING_KEY = "user:following:processing";
    public static final String SOCIAL_INTERACTION_STREAM_KEY = "social:interaction:stream";
    public static final String SOCIAL_INTERACTION_DLQ_KEY = "social:interaction:dlq";

    private static final String POST_LIKE_SET_KEY = "post:likes:users:";
    private static final String POST_LIKE_INIT_KEY = "post:likes:init:";
    private static final String USER_LIKED_POSTS_KEY = "user:likes:posts:";
    private static final String USER_LIKED_POSTS_INIT_KEY = "user:likes:init:";
    private static final String USER_FOLLOWING_KEY = "user:following:";
    private static final String USER_FOLLOWING_INIT_KEY = "user:following:init:";
    private static final String USER_FOLLOWERS_KEY = "user:followers:";
    private static final String USER_FOLLOWERS_INIT_KEY = "user:followers:init:";

    private InteractionRedisKeys() {
    }

    @NonNull
    public static String buildPostLikeSetKey(Integer postId) {
        return POST_LIKE_SET_KEY + postId;
    }

    @NonNull
    public static String buildPostLikeInitKey(Integer postId) {
        return POST_LIKE_INIT_KEY + postId;
    }

    @NonNull
    public static String buildUserLikedPostsKey(Integer userId) {
        return USER_LIKED_POSTS_KEY + userId;
    }

    @NonNull
    public static String buildUserLikedPostsInitKey(Integer userId) {
        return USER_LIKED_POSTS_INIT_KEY + userId;
    }

    @NonNull
    public static String buildFollowingKey(Integer followerId) {
        return USER_FOLLOWING_KEY + followerId;
    }

    @NonNull
    public static String buildFollowingInitKey(Integer followerId) {
        return USER_FOLLOWING_INIT_KEY + followerId;
    }

    @NonNull
    public static String buildFollowersKey(Integer followedId) {
        return USER_FOLLOWERS_KEY + followedId;
    }

    @NonNull
    public static String buildFollowersInitKey(Integer followedId) {
        return USER_FOLLOWERS_INIT_KEY + followedId;
    }

    @NonNull
    public static String toRedisValue(Integer value) {
        return Objects.requireNonNull(String.valueOf(value));
    }
}
