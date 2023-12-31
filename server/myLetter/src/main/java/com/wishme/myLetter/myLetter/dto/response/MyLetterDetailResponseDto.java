package com.wishme.myLetter.myLetter.dto.response;

import com.wishme.myLetter.user.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MyLetterDetailResponseDto {

    private Long myLetterSeq;

    private String toUserNickname;

    private String content;

    private Long fromUser;

    private String fromUserNickname;

    private Boolean canReply;

    private Boolean isMine;

    @Builder
    public MyLetterDetailResponseDto(Long myLetterSeq, String toUserNickname, String content, Long fromUser, String fromUserNickname, Boolean canReply, Boolean isMine) {
        this.myLetterSeq = myLetterSeq;
        this.toUserNickname = toUserNickname;
        this.content = content;
        this.fromUser = fromUser;
        this.fromUserNickname = fromUserNickname;
        this.canReply = canReply;
        this.isMine = isMine;
    }
}
