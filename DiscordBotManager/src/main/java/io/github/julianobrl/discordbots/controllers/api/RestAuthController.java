package io.github.julianobrl.discordbots.controllers.api;

import io.github.julianobrl.discordbots.entities.Profile;
import io.github.julianobrl.discordbots.entities.dtos.AuthRequest;
import io.github.julianobrl.discordbots.entities.dtos.AuthResponse;
import io.github.julianobrl.discordbots.services.JwtService;
import io.github.julianobrl.discordbots.services.ProfileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Authentication")
public class RestAuthController {

    private final AuthenticationManager authenticationManager; // Injetado do passo 2
    private final JwtService jwtService;
    private final ProfileService profileService;

    @PostMapping("/login")
    protected AuthResponse getIndex(@RequestBody AuthRequest authRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        if (authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Long userId = ((Profile) userDetails).getId();
            return jwtService.create(userId, userDetails.getUsername(), userDetails.getAuthorities());
        } else {
            throw new UsernameNotFoundException("Invalid username and/or password!");
        }
    }

    @PostMapping("/register")
    protected AuthResponse create(@RequestBody Profile profile){
        String password = profile.getPassword();
        profileService.create(profile);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(profile.getUsername(), password)
        );

        if (authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Long userId = ((Profile) userDetails).getId();
            return jwtService.create(userId, userDetails.getUsername(), userDetails.getAuthorities());
        } else {
            throw new UsernameNotFoundException("Invalid username and/or password!");
        }

    }

}
