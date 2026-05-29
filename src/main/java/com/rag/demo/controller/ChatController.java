package com.rag.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;

import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/chat")
public class ChatController {

    private final VectorStore vectorStore;
    private  final OllamaChatModel ollamaChatModel;

    @PostMapping("prompt")
    public String chat(@RequestBody String reqMessage){
       return ChatClient.builder(ollamaChatModel)
                .build()
                .prompt()
                .advisors(QuestionAnswerAdvisor.builder(vectorStore).build())
                .user(reqMessage)
                .call()
                .content();
    }

}
