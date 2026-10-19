package cl.duoc.barriodigital.audit;

import cl.duoc.barriodigital.audit.domain.EventoAuditoria;
import cl.duoc.barriodigital.audit.repo.EventoAuditoriaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    @Autowired
    EventoAuditoriaRepository repo;

    @Test
    void funcionario_puede_leer_el_timeline() throws Exception {
        mvc.perform(get("/audit/timeline").with(jwt().authorities(new SimpleGrantedAuthority("ROLE_Funcionario"))))
           .andExpect(status().isOk());
    }

    /**
     * El funcionario que atiende a un vecino ve lo que el vecino hizo y lo que le pasaron
     * a sus tramites, incluido el que otro funcionario ingreso a su nombre.
     */
    @Test
    void persona_trae_lo_que_hizo_y_lo_que_le_paso_mas_reciente_primero() throws Exception {
        Instant t0 = Instant.parse("2030-03-01T10:00:00Z");
        repo.save(new EventoAuditoria("p-1", 50L, "func9@duocuc.cl", null, "INGRESADO", t0,
                "vecino50@duocuc.cl", 1L, "Ingresado por func9@duocuc.cl a nombre de vecino50@duocuc.cl"));
        repo.save(new EventoAuditoria("p-2", 50L, "func8@duocuc.cl", "INGRESADO", "ADMITIDO", t0.plusSeconds(60),
                "vecino50@duocuc.cl", 1L, null));
        repo.save(new EventoAuditoria("p-3", 51L, "otro@duocuc.cl", null, "INGRESADO", t0,
                "otro@duocuc.cl", 1L, null));

        mvc.perform(get("/audit/timeline").param("persona", "VECINO50@duocuc.cl")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_Funcionario"))))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.length()").value(2))
           .andExpect(jsonPath("$[0].estadoNuevo").value("ADMITIDO"))
           .andExpect(jsonPath("$[1].usuario").value("func9@duocuc.cl"))
           .andExpect(jsonPath("$[1].vecinoUsername").value("vecino50@duocuc.cl"));
    }
}
