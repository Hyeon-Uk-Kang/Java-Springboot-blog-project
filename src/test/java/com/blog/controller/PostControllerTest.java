package com.blog.controller;

import com.blog.domain.Post;
import com.blog.repository.PostRepository;
import com.blog.request.PostCreate;
import com.blog.request.PostEdit;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
class PostControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void clear()
    {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("/posts 요청시 Hello World를 출력한다.")
    void test() throws Exception {

        PostCreate request=PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        String json = objectMapper.writeValueAsString(request);

        System.out.println(json);
        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string(""))
                .andDo(print());
    }

    @Test
    @DisplayName("/posts 요청시 title값은 필수다.")
    void test2() throws Exception {

        PostCreate request=PostCreate.builder()
                .content("내용입니다.")
                .build();

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                //.andExpect(jsonPath("$.code").value("400"))
                //.andExpect(jsonPath("$.message").value("잘못된 요청입니다."))
                //.andExpect(jsonPath("$.validation.title").value("타이틀을 입력하세요."))
                .andDo(print());
    }

    @Test
    @DisplayName("/posts 요청시 DB에 값이 저장된다.")
    void test3() throws Exception {

        PostCreate request=PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(print());

        assertEquals(1L,postRepository.count());

        Post post = postRepository.findAll().get(0);
        assertEquals("제목입니다.",post.getTitle());
        assertEquals("내용입니다.",post.getContent());
    }

    @Test
    @DisplayName("글 1개 조회")
    void test4() throws Exception {
        Post post = Post.builder()
                .title("12345")
                .content("bar")
                .build();

        postRepository.save(post);

        mockMvc.perform(get("/posts/{postId}",post.getId())
                    .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.title").value("12345"))
                .andExpect(jsonPath("$.content").value("bar"))
                .andDo(print());

    }

    @Test
    @DisplayName("글 여러개 조회")
    void test5() throws Exception {
        List<Post> requestPosts= IntStream.range(0,20)
                .mapToObj(i ->
                        Post.builder()
                                .title("foo"+i)
                                .content("bar"+i)
                                .build())
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        mockMvc.perform(get("/posts?page=1&size=10")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()",Matchers.is(10)))
                .andExpect(jsonPath("$[0].title").value("foo19"))
                .andExpect(jsonPath("$[0].content").value("bar19"))
                .andDo(print());

    }

    @Test
    @DisplayName("페이지를 0으로 요청하면 첫 페이지를 가져온다.")
    void test6() throws Exception {
        List<Post> requestPosts= IntStream.range(0,20)
                .mapToObj(i ->
                        Post.builder()
                                .title("foo"+i)
                                .content("bar"+i)
                                .build())
                .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        mockMvc.perform(get("/posts?page=0&size=10")
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()",Matchers.is(10)))
                .andExpect(jsonPath("$[0].title").value("foo19"))
                .andExpect(jsonPath("$[0].content").value("bar19"))
                .andDo(print());

    }

    @Test
    @DisplayName("글 제목 수정")
    void test7() throws Exception {

        Post post = Post.builder()
                .title("호돌맨")
                .content("반포자이")
                .build();

        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("호돌걸")
                .content("초가집")
                .build();

        mockMvc.perform(patch("/posts/{postId}",post.getId())
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postEdit)))
                .andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    @DisplayName("게시글 삭제")
    void test8() throws Exception {

        Post post = Post.builder()
                .title("호돌맨")
                .content("반포자이")
                .build();

        postRepository.save(post);

        mockMvc.perform(delete("/posts/{postId}",post.getId())
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회")
    void test9() throws Exception{

        mockMvc.perform(delete("/posts/{postId}",1L)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 수정")
    void test10() throws Exception{

        PostEdit postEdit = PostEdit.builder()
                .title("호돌맨")
                .content("반포자이")
                .build();

        mockMvc.perform(patch("/posts/{postId}",1L)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postEdit)))
                        .andExpect(status().isNotFound())
                        .andDo(print());
    }

    @Test
    @DisplayName("게시글 작성시 제목에 '바보'는 포함될 수 없다.")
    void test11() throws Exception {

        PostCreate request=PostCreate.builder()
                .title("나는 바보입니다.")
                .content("반포자이")
                .build();

        String json = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/posts")
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andDo(print());

    }
}