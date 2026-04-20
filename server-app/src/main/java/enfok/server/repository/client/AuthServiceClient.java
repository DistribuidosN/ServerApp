package enfok.server.repository.client;

import enfok.server.model.entity.dto.auth.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "auth-api")
public interface AuthServiceClient {

    @POST
    @Path("/auth/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    LoginResponse login(LoginRequest request);

    @POST
    @Path("/auth/register")
    @Consumes(MediaType.APPLICATION_JSON)
    Response register(RegisterRequest request);

    @POST
    @Path("/auth/logout")
    Response logout(@HeaderParam("Authorization") String token);

    @POST
    @Path("/auth/forget-password")
    @Consumes(MediaType.APPLICATION_JSON)
    Response forgetPassword(ForgetPasswordRequest request);

    @POST
    @Path("/auth/reset-password")
    @Consumes(MediaType.APPLICATION_JSON)
    Response resetPassword(@HeaderParam("Authorization") String token, ResetPasswordRequest request);

    @POST
    @Path("/auth/validate")
    @Produces(MediaType.APPLICATION_JSON)
    ValidateResponse validate(@HeaderParam("Authorization") String token);
}
