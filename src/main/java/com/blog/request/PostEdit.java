package com.blog.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@Builder
public class PostEdit {

    @NotBlank(message="타이틀을 입력하세요.")
    private String title;
    @NotBlank
    private String content;

    @Builder
    public PostEdit(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
