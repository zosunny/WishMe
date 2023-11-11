package com.wishme.myLetter.util;

import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

public class BadWordFilterUtil extends HashSet<String> implements BadWords{
    private String substituteValue = "*";

    //대체 문자 지정
    //기본값 : *
    public BadWordFilterUtil() {
        addAll(List.of(koreaBadWords));
    }

    public BadWordFilterUtil(String substituteValue) {
        this.substituteValue = substituteValue;
        addAll(List.of(koreaBadWords));
    }

    //비속어 있다면 대체
    public String change(String text) {
        String[] badWords = stream().filter(text::contains).toArray(String[]::new);
        for (String v : badWords) {
            String sub = this.substituteValue.repeat(v.length());
            text = text.replace(v, sub);
        }
        return text;
    }

    public String change(String text, String[] sings) {
        StringBuilder singBuilder = new StringBuilder("[");
        for (String sing : sings) singBuilder.append(Pattern.quote(sing));
        singBuilder.append("]*");
        String patternText = singBuilder.toString();

        for (String word : this) {
            if (word.length() == 1) text = text.replace(word, substituteValue);
            String[] chars = word.chars().mapToObj(Character::toString).toArray(String[]::new);
            text = Pattern.compile(String.join(patternText, chars))
                    .matcher(text)
                    .replaceAll(v -> substituteValue.repeat(v.group().length()));
        }

        return text;
    }

    //비속어가 1개라도 존재하면 true 반환
    public boolean check(String text) {
        return stream().anyMatch(text::contains);
    }

    //공백을 없는 상태 체크
    public boolean blankCheck(String text) {
        return check(text.replace(" ", ""));
    }
}
