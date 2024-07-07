package middle_point_search.backend.common.exception;

import middle_point_search.backend.common.exception.errorCode.UserErrorCode;

public class DuplicateVoteRoomException extends CustomException {

    public DuplicateVoteRoomException() {
        super(UserErrorCode.DUPLICATE_VOTE_ROOM);
    }
}
