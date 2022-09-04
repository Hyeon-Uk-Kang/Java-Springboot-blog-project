package com.blog.service;

import com.blog.domain.Post;
import com.blog.exception.PostNotFound;
import com.blog.repository.PostRepository;
import com.blog.request.PostCreate;
import com.blog.request.PostEdit;
import com.blog.request.PostSearch;
import com.blog.response.PostResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.data.domain.Sort.Direction.*;

@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void clean()
    {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("글 작성")
    void test1(){
        PostCreate postCreate = PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        postService.write(postCreate);

        assertEquals(1L,postRepository.count());
        Post post = postRepository.findAll().get(0);
        assertEquals("제목입니다.",post.getTitle());
        assertEquals("내용입니다.",post.getContent());
    }

    @Test
    @DisplayName("글 1개 조회")
    void test2()
    {
        Post requestPost = Post.builder()
                .title("foo")
                .content("bar")
                .build();

        postRepository.save(requestPost);

        PostResponse response = postService.get(requestPost.getId());

        assertNotNull(response);
        assertEquals("foo",response.getTitle());
        assertEquals("bar",response.getContent());
    }

    @Test
    @DisplayName("글 여러개 조회")
    void test3()
    {
        List<Post> requestPosts= IntStream.range(0,20)
                .mapToObj(i ->
                            Post.builder()
                                    .title("foo"+i)
                                    .content("bar1"+i)
                                    .build())
                    .collect(Collectors.toList());
        postRepository.saveAll(requestPosts);

        PostSearch postSearch=PostSearch.builder()
                .page(1)
                .size(10)
                .build();

        List<PostResponse> posts = postService.getList(postSearch);

        assertEquals(10L,posts.size());
        assertEquals("foo19",posts.get(0).getTitle());
    }

    @Test
    @DisplayName("글 제목 수정")
    void test4()
    {
        Post post = Post.builder()
                .title("호돌맨")
                .content("반포자이")
                .build();

        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("호돌걸")
                .content("반포자이")
                .build();

        postService.edit(post.getId(),postEdit);

        Post changedPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다. id=" + post.getId()));
        assertEquals("호돌걸",changedPost.getTitle());
        assertEquals("반포자이",changedPost.getContent());
    }

    @Test
    @DisplayName("글 내용 수정")
    void test5()
    {
        Post post = Post.builder()
                .title("호돌맨")
                .content("반포자이")
                .build();

        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("호돌걸")
                .content("초가집")
                .build();

        postService.edit(post.getId(),postEdit);

        Post changedPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다. id=" + post.getId()));
        //assertEquals("호돌맨",changedPost.getTitle());
        assertEquals("초가집",changedPost.getContent());
    }

    @Test
    @DisplayName("게시글 삭제")
    void test6()
    {
        Post post = Post.builder()
                .title("호돌맨")
                .content("반포자이")
                .build();

        postRepository.save(post);

        postService.delete(post.getId());

        //assertEquals("호돌맨",changedPost.getTitle());
        assertEquals(0,postRepository.count());
    }

    @Test
    @DisplayName("글 1개 조회 - 존재하지 않는 글")
    void test7()
    {
        Post post = Post.builder()
                .title("호돌맨")
                .content("반포자이")
                .build();

        postRepository.save(post);

        assertThrows(PostNotFound.class,()->{
            postService.get(post.getId()+1L);
        });
    }

    @Test
    @DisplayName("게시글 삭제 - 존재하지 않는 글")
    void test8()
    {
        Post post = Post.builder()
                .title("호돌맨")
                .content("반포자이")
                .build();

        postRepository.save(post);

        assertThrows(PostNotFound.class,()->{
            postService.delete(post.getId()+1L);
        });
    }

    @Test
    @DisplayName("글 내용 수정 - 존재하지 않는 글")
    void test9()
    {
        Post post = Post.builder()
                .title("호돌맨")
                .content("반포자이")
                .build();

        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("호돌걸")
                .content("초가집")
                .build();

        assertThrows(PostNotFound.class, ()->{
            postService.edit(post.getId()+1L, postEdit);
        });
    }
}