package view;

import model.UserSession;

public interface SessionAware {
    void setSession(UserSession session);
}
