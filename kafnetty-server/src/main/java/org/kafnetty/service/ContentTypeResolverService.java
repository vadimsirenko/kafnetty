package org.kafnetty.service;

import java.io.File;

public interface ContentTypeResolverService {
    String getMimeType(File file);
}
