package com.blog.request;

import com.blog.exception.InvalidRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Setter
@Getter
@ToString
public class PostCreate {

    @NotBlank(message="타이틀을 입력하세요.")
    private String title;
    @NotBlank
    private String content;

    @Builder
    public PostCreate(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void validate()
    {
        if(title.contains("바보")){
            throw new InvalidRequest("title","제목에 바보를 포함할 수 없습니다.");
        }
    }
}
