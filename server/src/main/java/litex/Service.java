package litex;

import litex.service.*;

public class Service {
    public static final UserService user = new UserService();
    public static final NotificationService notification = new NotificationService();
    public static final TweetService tweet = new TweetService(notification);
    public static final TrendService trend = new TrendService();
    public static final MessageService message = new MessageService();
    public static final SearchService search = new SearchService();
    public static final UploadService upload = new UploadService();
    public static final BookmarkService bookmark = new BookmarkService();
    public static final BlockService block = new BlockService();
    public static final ReportService report = new ReportService();
    public static final PollService poll = new PollService();
}
