package dump.sh.minesweeper.objects;

public class User {
    public String id, team_id, name, color, real_name, tz, tz_label;
    public boolean deleted, is_admin, is_owner, is_primary_owner, is_restricted, is_ultra_restricted, is_bot;
    public boolean is_app_user, has_2fa;
    public Profile profile;
    public long updated, tz_offset;

    public class Profile {
        public String avatar_hash, status_text, status_emoji, real_name, display_name, real_name_normalized;
        public String display_name_normalized, email, image_24, image_32, image_48, image_72, image_192, image_512;
        public String team, title, phone, skype, image_original, image_1024, status_text_canonical;
        public boolean is_custom_image;
        public Integer status_expiration;
    }
}
