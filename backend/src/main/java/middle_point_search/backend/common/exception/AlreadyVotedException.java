package middle_point_search.backend.common.exception;

import middle_point_search.backend.common.exception.errorCode.UserErrorCode;

public class AlreadyVotedException extends CustomException {
    public AlreadyVotedException() {
        super(UserErrorCode.ALREADY_VOTED);
    }
}

