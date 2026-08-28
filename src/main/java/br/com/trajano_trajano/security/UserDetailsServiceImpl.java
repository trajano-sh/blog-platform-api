package br.com.trajano_trajano.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import br.com.trajano_trajano.shared.exception.NotFoundException;
import br.com.trajano_trajano.user.UserRepository;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("Usuário não encontrado"));
    }
}
