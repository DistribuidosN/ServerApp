package enfok.server.utility;

import enfok.server.config.Config;
import enfok.server.model.entity.bd.BatchWithCover;
import enfok.server.model.entity.bd.Image;
import enfok.server.model.entity.bd.PaginatedImages;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class UrlIpRefactor {

    @Inject
    Config config;

    /**
     * Replaces the host/IP part of a URL with the configured SERVER_IP.
     * Example: http://192.168.80.62/enfok-images/abc.jpg -> http://192.168.80.60/enfok-images/abc.jpg
     */
    public String refactorUrl(String url) {
        if (url == null || url.isEmpty()) {
            return url;
        }
        String serverIp = config.getServerIP();
        if (serverIp == null || serverIp.isEmpty()) {
            return url;
        }

        // If serverIp already contains a protocol (e.g., http://1.2.3.4), 
        // we replace the entire protocol + host part of the original URL.
        if (serverIp.contains("://")) {
            return url.replaceFirst("^https?://[^/]+", serverIp);
        }

        // Otherwise, we preserve the original protocol and just replace the host part.
        return url.replaceFirst("^(https?://)[^/]+", "$1" + serverIp);
    }

    /**
     * Refactors cover image URLs in a list of BatchWithCover objects.
     */
    public List<BatchWithCover> refactorBatchWithCovers(List<BatchWithCover> batches) {
        if (batches == null) return null;
        for (BatchWithCover b : batches) {
            if (b.getCoverImageUrl() != null) {
                b.setCoverImageUrl(refactorUrl(b.getCoverImageUrl()));
            }
        }
        return batches;
    }

    /**
     * Refactors result paths (URLs) in a PaginatedImages object.
     */
    public PaginatedImages refactorPaginatedImages(PaginatedImages paginated) {
        if (paginated == null || paginated.getImages() == null) return paginated;
        for (Image img : paginated.getImages()) {
            if (img.getResultPath() != null) {
                img.setResultPath(refactorUrl(img.getResultPath()));
            }
        }
        return paginated;
    }
}
