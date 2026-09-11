package cl.duoc.barriodigital.audit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuditApiSecurityTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    JwtDecoder jwtDecoder;

    @Test
    void sin_token_401() throws Exception {
        mvc.perform(get("/audit/timeline")).andExpect(status().isUnauthorized());
    }

    @Test
    void auditor_puede_leer_el_timeline() throws Exception {
        mvc.perform(get("/audit/timeline").with(jwt().authorities(new SimpleGrantedAuthority("ROLE_Auditor"))))
           .andExpect(status().isOk());
    }

    @Test
    void vecino_no_puede_leer_el_timeline_403() throws Exception {
        mvc.perform(get("/audit/timeline").with(jwt().authorities(new SimpleGrantedAuthority("ROLE_Vecino"))))
           .andExpect(status().isForbidden());
    }
}
