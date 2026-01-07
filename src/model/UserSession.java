package model;

public class UserSession {
    private final UserRole role;
    private final String userId;
    private final String displayName;

    public UserSession(UserRole role, String userId, String displayName) {
        this.role = role;
        this.userId = userId;
        this.displayName = displayName;
    }

    public UserRole getRole() {
        return role;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }
}
