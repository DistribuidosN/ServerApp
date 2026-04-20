package enfok.server.repository.client;

import enfok.server.model.entity.dto.user.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "auth-api") // Same as Auth since they share base URL
public interface UserServiceClient {

    @GET
    @Path("/user/profile")
    @Produces(MediaType.APPLICATION_JSON)
    UserProfileResponse getProfile(@HeaderParam("Authorization") String token);

    @PUT
    @Path("/user/profile")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    UserUpdateResponse updateProfile(@HeaderParam("Authorization") String token, UserUpdateRequest request);

    @DELETE
    @Path("/user/account")
    Response deleteAccount(@HeaderParam("Authorization") String token);

    @GET
    @Path("/user/search")
    @Produces(MediaType.APPLICATION_JSON)
    UserProfileResponse searchUser(@HeaderParam("Authorization") String token, @QueryParam("username") String username);
}
