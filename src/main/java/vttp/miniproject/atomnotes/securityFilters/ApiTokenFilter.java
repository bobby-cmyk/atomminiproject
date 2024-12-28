package vttp.miniproject.atomnotes.securityFilters;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vttp.miniproject.atomnotes.models.AuthUserDetails;
import vttp.miniproject.atomnotes.models.UserEntity;
import vttp.miniproject.atomnotes.repositories.UserRepo;
import vttp.miniproject.atomnotes.services.ApiService;

@Component
public class ApiTokenFilter extends OncePerRequestFilter {
    
    @Autowired
    private ApiService apiSvc;

    @Autowired
    private UserRepo userRepo;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request, 
        HttpServletResponse response, 
        FilterChain filterChain) throws ServletException, IOException 
    {
        try {
            String header = request.getHeader("Authorization");

            if (header != null && header.startsWith("Bearer ")) {
                // The substring after "Bearer "
                String token = header.substring(7);

                if (apiSvc.tokenExist(token)) {
                    String userId = apiSvc.retrieveByToken(token);
                    UserEntity user = userRepo.getUserEntity(userId);

                    AuthUserDetails authUser = new AuthUserDetails(
                        user.getId(),
                        user.getUsername(),
                        user.getPassword(),  
                        List.of(() -> "ROLE_" + user.getRole()));

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                authUser, 
                                null, 
                                authUser.getAuthorities());

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    throw new SecurityException("Token does not exist");
                }
            }

            filterChain.doFilter(request, response);

        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Bad Request: " + e.getMessage());
        } catch (SecurityException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: " + e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Internal Server Error: " + e.getMessage());
        }
    }
}

