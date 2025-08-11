package es.cic.curso25.proy011.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.cic.curso25.proy011.exceptions.NotFoundException;
import es.cic.curso25.proy011.model.Mesa;
import es.cic.curso25.proy011.model.Silla;
import es.cic.curso25.proy011.repository.MesaRepository;
import jakarta.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MesaServiceIntegrationTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MesaService mesaService;

    @Autowired
    MesaRepository mesaRepository;

    Plaza mesa;
    List<Coche> sillas;
    Plaza mesaGuardada;

    @BeforeEach
    void preparacion() {
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

        mesaGuardada = mesaService.postMesa(mesa);
    }

    @Test
    void testDeleteMesa() {
        mesaService.deleteMesa(mesaGuardada.getId());

        Optional<Plaza> mesaBorrada = mesaRepository.findById(mesaGuardada.getId());

        assertTrue(mesaBorrada.isEmpty());

        assertThrows(NotFoundException.class, () -> {
            mesaService.getMesa(mesaGuardada.getId());
        });
    }

    // Con esto + el beforeEach, estaría probando también el método post
    @Test
    void testGetMesa() {
        Plaza mesaObtenida = mesaService.getMesa(mesaGuardada.getId());
        assertEquals(mesaObtenida.getColor(), mesaGuardada.getColor());
        assertEquals(mesaObtenida.getId(), mesaGuardada.getId());
        assertEquals(mesaObtenida.getMaterial(), mesaGuardada.getMaterial());
        assertEquals(mesaObtenida.getNumPatas(), mesaGuardada.getNumPatas());
        assertEquals(mesaObtenida.getForma(), mesaGuardada.getForma());
    }

    @Test
    void testGetAllMesas() {
        List<Plaza> todasLasMesas = mesaService.getAllMesas();
        assertTrue(todasLasMesas.size() >= 1);
    }

    @Test
    void testGetSillasDeMesa(){

    }

    @Test
    @Transactional
    void testAgregarSilla() {
        int numInicialSillas = mesaGuardada.getSillas().size();

        Coche sillaNueva = new Coche(3, true, "azul");

        Plaza mesaConSilla = mesaService.agregarSilla(sillaNueva, mesaGuardada.getId());

        assertTrue(mesaConSilla.getSillas().size() == numInicialSillas + 1);
    }

    @Test
    @Transactional
    void testUpdateMesa() {
        List<Coche> nuevasSillas = new ArrayList<>();
        Coche nuevSilla1 = new Coche(5, true, "marron");
        Coche nuevSilla2 = new Coche(6, false, "verde");
        Coche nuevSilla3 = new Coche(3, true, "blanco");

        nuevasSillas.add(nuevSilla1);
        nuevasSillas.add(nuevSilla2);
        nuevasSillas.add(nuevSilla3);

        Plaza mesaActualizada = new Plaza("morada", "cuadrada", 7, "granito");

        mesaActualizada.setSillas(nuevasSillas);

        Long id = mesaGuardada.getId();

        mesaService.updateMesa(id, mesaActualizada);

        Plaza mesaDesdeBD = mesaService.getMesa(mesaGuardada.getId());

        assertEquals(mesaActualizada.getColor(), mesaDesdeBD.getColor());
        assertEquals(mesaActualizada.getForma(), mesaDesdeBD.getForma());
        assertEquals(mesaGuardada.getId(), mesaDesdeBD.getId());
        assertEquals(mesaActualizada.getMaterial(), mesaDesdeBD.getMaterial());
        assertEquals(mesaActualizada.getNumPatas(), mesaDesdeBD.getNumPatas());
        assertEquals(mesaActualizada.getSillas().size(), mesaDesdeBD.getSillas().size());
    }

    @Test
    @Transactional
    void testUpdateSillaEnMesa() {
        Coche sillaActualizada = new Coche(12, true, "plateado");
        mesaService.updateSillaEnMesa(mesaGuardada.getSillas().get(0).getId(), mesaGuardada.getId(), sillaActualizada);
        
        Plaza mesaActualizada = mesaService.getMesa(mesaGuardada.getId());
        List<Coche> todasLasSillas = mesaActualizada.getSillas();

        Optional<Coche> sillaBuscada = todasLasSillas.stream()
                .filter(s -> s.getColor().equalsIgnoreCase("plateado"))
                .filter(Coche::isRespaldo)
                .filter(s -> s.getNumPatas() == 12)
                .findFirst();

        assertTrue(sillaBuscada.isPresent());
    }
}
