package org.kafnetty.service;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class ContentTypeResolverServiceImpl implements ContentTypeResolverService {
    private final Tika tika = new Tika();

    @Override
    public String getMimeType(File file) {
        try {
            return tika.detect(file);
        } catch (IOException e) {
            return "text/plain;charset=utf-8";
        }
    }
}
