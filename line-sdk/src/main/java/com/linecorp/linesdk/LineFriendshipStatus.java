package com.linecorp.linesdk;

/**
 * Represents the friendship status between a LINE Official Account and a user.
 */
public class LineFriendshipStatus {
    private boolean friendFlag;

    /**
     * Constructs a new {@link LineFriendshipStatus} instance.
     * @param friendFlag Whether the LINE Official Account is a friend of the user or not.
     */
    public LineFriendshipStatus(final boolean friendFlag) {
        this.friendFlag = friendFlag;
    }

    /**
     * Gets the friendship status of the user and the LINE Official Account linked to your LINE Login channel.
     * @return True if the user has added the LINE Official Account as a friend and has not blocked
     *         the LINE Official Account; false otherwise.
     */
    public boolean isFriend() {
        return friendFlag;
    }

    /**
     * @hide
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        LineFriendshipStatus that = (LineFriendshipStatus) o;

        return friendFlag == that.friendFlag;
    }

    /**
     * @hide
     */
    @Override
    public int hashCode() {
        return friendFlag ? 1 : 0;
    }

    /**
     * @hide
     */
    @Override
    public String toString() {
        return "LineFriendshipStatus{" +
               "friendFlag=" + friendFlag +
               '}';
    }
}
