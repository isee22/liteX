# Implementation Plan

## Phase 1: JWT Middleware + Search

- [x] 1. Implement JWT Authentication Middleware






  - [x] 1.1 Create AuthMiddleware class extending MiddlewarePlugin

    - Implement handle() method to check Authorization header
    - Extract and validate JWT token using JwtUtil
    - Store userid in Context for downstream handlers
    - Support excludePaths configuration for public routes
    - _Requirements: 1.1, 1.2, 1.3_

  - [x] 1.2 Register AuthMiddleware in Application.java

    - Configure excludePaths for /api/auth/* routes
    - Apply middleware before route handlers
    - _Requirements: 1.4_

- [x] 2. Implement Search Functionality





  - [x] 2.1 Add search methods to TweetMapper


    - Add searchByContent() method with LIKE query
    - Support pagination with offset and limit
    - _Requirements: 2.1, 2.3_


  - [x] 2.2 Add search methods to UserMapper





    - Add searchByNameOrNickname() method with LIKE query


    - Support pagination with offset and limit
    - _Requirements: 3.1_
  - [x] 2.3 Create SearchService class





    - Implement searchTweets() method with user info and like status
    - Implement searchUsers() method with follow status
    - Handle empty/whitespace queries by returning empty list
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 3.1, 3.2, 3.3, 4.1, 4.2, 4.3_

- [x] 3. Create Search API Endpoints






  - [x] 3.1 Create SearchController class

    - Implement GET /api/search/tweets endpoint
    - Implement GET /api/search/users endpoint
    - Parse query parameters: q, page, size
    - _Requirements: 2.1, 3.1, 4.1, 4.2, 4.3_
  - [x] 3.2 Register SearchController routes in Application.java


    - Add SearchService to Service.java
    - Register search routes
    - _Requirements: 2.1, 3.1_

---

## Phase 2: Followers/Following, Upload, Hashtag, Mention

- [x] 4. Implement Followers/Following Lists





  - [x] 4.1 Add follower/following query methods to FollowMapper


    - Add findFollowers() method with pagination
    - Add findFollowing() method with pagination
    - _Requirements: 5.1, 6.1_

  - [x] 4.2 Add getFollowers/getFollowing methods to UserService

    - Return user list with follow status for current user
    - Support pagination
    - _Requirements: 5.2, 5.3, 6.2, 6.3_


  - [x] 4.3 Add follower/following endpoints to UserController





    - GET /api/user/:id/followers
    - GET /api/user/:id/following
    - _Requirements: 5.1, 6.1_

- [x] 5. Implement Image Upload





  - [x] 5.1 Create UploadService class


    - Implement uploadImage() method to save file and return URL
    - Implement validateFileType() for jpg, png, gif, webp
    - Implement validateFileSize() for max 5MB
    - Read upload path from application.yml config
    - _Requirements: 7.1, 7.2, 7.3, 7.4_
  - [x] 5.2 Create UploadController class


    - Implement POST /api/upload/image endpoint
    - Handle multipart form data
    - _Requirements: 7.1_
  - [x] 5.3 Register UploadController and add config to application.yml


    - Add upload.path and upload.maxSize config
    - Register routes in Application.java
    - _Requirements: 7.1_

- [x] 6. Implement Hashtag Parsing





  - [x] 6.1 Add parseHashtags() method to TweetService


    - Extract #hashtag patterns using regex
    - Return list of hashtag strings (without #)
    - _Requirements: 8.1_
  - [x] 6.2 Update TweetService.create() to process hashtags


    - Parse hashtags from content
    - Update trend counts for each hashtag
    - _Requirements: 8.2_
  - [x] 6.3 Add hashtag search to SearchController


    - GET /api/search/hashtag/:tag
    - Reuse searchTweets with hashtag filter
    - _Requirements: 8.3_

- [x] 7. Implement @Mention Parsing





  - [x] 7.1 Add parseMentions() method to TweetService


    - Extract @username patterns using regex
    - Return list of username strings (without @)
    - _Requirements: 9.1_
  - [x] 7.2 Update TweetService.create() to process mentions


    - Parse mentions from content
    - Create notifications for mentioned users
    - _Requirements: 9.2_
