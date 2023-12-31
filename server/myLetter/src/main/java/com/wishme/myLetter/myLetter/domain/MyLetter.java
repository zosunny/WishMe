package com.wishme.myLetter.myLetter.domain;

import com.wishme.myLetter.asset.domain.Asset;
import com.wishme.myLetter.common.domain.BaseTimeEntity;
import com.wishme.myLetter.user.domain.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "myletter")
public class MyLetter extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "my_letter_seq", unique = true)
    private Long myLetterSeq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user", nullable = false)
    private User toUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_seq", nullable = false)
    private Asset asset;

    @Column(columnDefinition = "text", nullable = false)
    private String content;

    @Column(name = "from_user_nickname", nullable = false)
    private String fromUserNickname;

    @Column(name = "from_user")
    private Long fromUser;

    @Column(name = "is_public", nullable = false, columnDefinition = "TINYINT(1) default 1")
    private Boolean isPublic;

    @OneToOne(mappedBy = "myLetter")
    private Reply reply;

    @Column(name = "is_report", nullable = false, columnDefinition = "TINYINT(1) default 0")
    private boolean isReport;

    @Column(name = "is_bad", nullable = false, columnDefinition = "TINYINT(1) default 0")
    private boolean isBad;

    @Builder
    public MyLetter(Long myLetterSeq, User toUser, Asset asset, String content, String fromUserNickname, Long fromUser, Boolean isPublic, Reply reply, boolean isBad) {
        this.myLetterSeq = myLetterSeq;
        this.toUser = toUser;
        this.asset = asset;
        this.content = content;
        this.fromUserNickname = fromUserNickname;
        this.fromUser = fromUser;
        this.isPublic = isPublic;
        this.reply = reply;
        this.isBad = isBad;
    }

    /**
     * 신고 반영
     */
    public void updateReport() {
        this.isReport = true;
        this.isPublic = false;
    }
}
