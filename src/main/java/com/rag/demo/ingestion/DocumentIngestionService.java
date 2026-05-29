package com.rag.demo.ingestion;


import lombok.RequiredArgsConstructor;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentIngestionService implements CommandLineRunner {

    @Value("classpath:/docs/gta_cheat_codes_master_list.pdf")
    private  Resource pdf;
    public final VectorStore vectorStore;
    @Override
    public void run(String... args) throws Exception {
        TikaDocumentReader tikaDocumentReader=new TikaDocumentReader(pdf);
        TextSplitter textSplitter=new TokenTextSplitter();
        List<Document> documents=textSplitter.split(tikaDocumentReader.read());
        vectorStore.accept(documents);
    }
}
