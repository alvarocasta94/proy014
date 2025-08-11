package es.cic.curso25.proy011.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.cic.curso25.proy011.model.Mesa;
import es.cic.curso25.proy011.model.Silla;
import es.cic.curso25.proy011.repository.MesaRepository;
import es.cic.curso25.proy011.service.MesaService;
import jakarta.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MesaControllerIntegrationTest {

    @Autowired
    MesaRepository mesaRepository;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MesaService mesaService;

    Plaza mesa;
    List<Coche> sillas;
    Plaza mesaGuardada;

    @BeforeEach
    void previa() {
        mesaRepository.deleteAll();
        mesa = new Plaza("azul", "redonda", 4, "madera");
        sillas = new ArrayList<>();

        Coche silla1 = new Coche(3, true, "azul");
        sillas.add(silla1);

        Coche silla2 = new Coche(4, false, "azul");
        sillas.add(silla2);

        Coche silla3 = new Coche(3, true, "blanco");
        sillas.add(silla3);

        Coche silla4 = new Coche(5, true, "verde");
        sillas.add(silla4);

        for (int i = 0; i < sillas.size(); i++) {
            mesa.addSilla(sillas.get(i));
        }

        mesaGuardada = mesaRepository.save(mesa);
    }

    @Test
    @Transactional
    void testAgregarSilla() throws Exception {
        Coche nuevaSilla = new Coche(23, true, "granate");

        int numInicialSillas = mesaGuardada.getSillas().size();

        mockMvc.perform(post("/mesa/" + mesaGuardada.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(nuevaSilla)))
                .andExpect(status().isOk()).andExpect(result -> {
                    Optional<Plaza> mesaObtenida = mesaRepository.findById(mesaGuardada.getId());
                    // Mesa mesaObtenida =
                    // objectMapper.readValue(result.getResponse().getContentAsString(),
                    // Mesa.class);
                    assertEquals((numInicialSillas + 1), mesaObtenida.get().getSillas().size());
                });
    }

    @Test
    void testDeleteMesa() throws Exception {
        mockMvc.perform(delete("/mesa/" + mesaGuardada.getId())
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Optional<Plaza> mesaBorrada = mesaRepository.findById(mesaGuardada.getId());
                    assertTrue(mesaBorrada.isEmpty());
                });

        mockMvc.perform(get("/mesa/" + mesaGuardada.getId())
                .contentType("application/json"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllMesas() throws Exception {
        mockMvc.perform(get("/mesa"))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    List<Plaza> mesasObtenidas = objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            new TypeReference<List<Plaza>>() {
                            });

                    assertNotNull(mesasObtenidas);
                    assertFalse(mesasObtenidas.isEmpty(), "La lista de mesas no debería estar vacía");
                });
    }

    @Test
    void testGetMesa() throws Exception {
        mockMvc.perform(get("/mesa/" + mesa.getId())
                .contentType("application/json"))
                .andExpect(result -> {
                    Plaza mesaObtenida = objectMapper.readValue(result.getResponse().getContentAsString(), Plaza.class);
                    assertEquals(mesa.getColor(), mesaObtenida.getColor());
                    assertEquals(mesa.getForma(), mesaObtenida.getForma());
                    assertEquals(mesa.getNumPatas(), mesaObtenida.getNumPatas());
                    assertEquals(mesa.getMaterial(), mesaObtenida.getMaterial());
                })
                .andDo(print())
                .andDo(print());
    }

    @Test
    void testPostMesa() throws Exception {
        Plaza mesaACrear = new Plaza("verde", "pentágono", 5, "madera");

        mockMvc.perform(post("/mesa")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mesaACrear)))
                .andExpect(result -> {
                    Plaza mesaObtenida = objectMapper.readValue(result.getResponse().getContentAsString(), Plaza.class);
                    assertEquals(mesaACrear.getColor(), mesaObtenida.getColor());
                    assertEquals(mesaACrear.getForma(), mesaObtenida.getForma());
                    assertEquals(mesaACrear.getNumPatas(), mesaObtenida.getNumPatas());
                    assertEquals(mesaACrear.getMaterial(), mesaObtenida.getMaterial());
                });
    }

    @Test
    void testUpdateMesa() throws Exception {
        Plaza mesaActualizada = new Plaza("verde", "pentágono", 5, "madera");

        mockMvc.perform(put("/mesa/" + mesaGuardada.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(mesaActualizada)))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    Plaza mesaObtenida = objectMapper.readValue(result.getResponse().getContentAsString(), Plaza.class);
                    assertEquals(mesaActualizada.getColor(), mesaObtenida.getColor());
                    assertEquals(mesaActualizada.getForma(), mesaObtenida.getForma());
                    assertEquals(mesaActualizada.getNumPatas(), mesaObtenida.getNumPatas());
                    assertEquals(mesaActualizada.getMaterial(), mesaObtenida.getMaterial());
                });
    }

    @Test
    void testUpdateSillaEnMesa() throws Exception {
        Coche sillaOriginal = mesaGuardada.getSillas().get(0);
        Long idSilla = sillaOriginal.getId();

        // Creamos una nueva versión de esa silla
        Coche sillaActualizada = new Coche(10, false, "negro");

        mockMvc.perform(put("/mesa/" + mesaGuardada.getId() + "/" + idSilla)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(sillaActualizada)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(result -> {
                    Plaza mesaExistente = objectMapper.readValue(result.getResponse().getContentAsString(),
                            Plaza.class);

                    // Optional<Mesa> mesaObtenida = mesaRepository.findById(mesaGuardada.getId());
                    // assertTrue(mesaObtenida.isPresent(), "La mesa debería existir después de la
                    // actualización");
                    // Mesa mesaExistente = mesaObtenida.get();
                    // assertEquals(mesaGuardada.getSillas().size(),
                    // mesaExistente.getSillas().size());

                    // Buscamos la silla actualizada por su ID
                    Coche sillaModificada = mesaExistente.getSillas().stream()
                            .filter(s -> s.getId().equals(idSilla))
                            .findFirst()
                            .orElseThrow(() -> new AssertionError("No se encontró la silla actualizada"));

                    assertEquals(sillaActualizada.getNumPatas(), sillaModificada.getNumPatas());
                    assertEquals(sillaActualizada.getColor(), sillaModificada.getColor());
                });
    }
}
