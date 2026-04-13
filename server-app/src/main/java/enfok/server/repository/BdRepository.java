package enfok.server.repository;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;

import enfok.server.utility.NetworkValidator;
import enfok.server.config.Config;
import enfok.server.ports.adapter.BdRepositoryInterface;
import enfok.server.model.entity.bd.Batches;
import enfok.server.model.entity.bd.Image;
import enfok.server.error.InfrastructureOfflineException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class BdRepository implements BdRepositoryInterface {

    @Inject
    Config config;

    @Inject
    NetworkValidator networkValidator;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private String getBaseUrl() {
        String url = config.getBdSystem();
        if (url != null && url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        }
        return url;
    }

    @Override
    public boolean validateServer() throws InfrastructureOfflineException {
        return networkValidator.validate(getBaseUrl());
    }

    @Override
    public String getNodeStatus(String token) throws InfrastructureOfflineException {
        if (config.isMockServices()) return "ACTIVE_MOCK_MODE";

        validateServer();
        // Pendiente HTTP real a Python/Go
        return "ACTIVE";
    }

    @Override
    public String getTransformations(String token) throws InfrastructureOfflineException {
        if (config.isMockServices()) return "BLUR, GRAYSCALE, INVERT, MOCK";

        validateServer();
        // Pendiente HTTP real a Python/Go
        return "BLUR, GRAYSCALE";
    }

    @Override
    public ArrayList<Image> getUserImages(String token, int limit, int offset) throws InfrastructureOfflineException {
        if (config.isMockServices()) {
            ArrayList<Image> imgs = new ArrayList<>();
            Image img = new Image();
            img.setOriginalName("mock_image.jpg");
            imgs.add(img);
            return imgs;
        }

        validateServer();
        // Pendiente HTTP real
        return new ArrayList<>();
    }

    @Override
    public ArrayList<Batches> getUserBatches(String token, int limit, int offset) throws InfrastructureOfflineException {
        if (config.isMockServices()) {
            ArrayList<Batches> batches = new ArrayList<>();
            Batches batch = new Batches();
            batch.setId(1);
            batch.setStatusId(2);
            batches.add(batch);
            return batches;
        }

        validateServer();
        // Pendiente HTTP real
        return new ArrayList<>();
    }
}
