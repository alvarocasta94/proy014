package es.cic.curso25.proy011.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.cic.curso25.proy011.model.Mesa;
import es.cic.curso25.proy011.model.Silla;
import es.cic.curso25.proy011.service.MesaService;

@RestController
@RequestMapping("/mesa")
public class MesaController {
    private static final Logger LOGGER = LoggerFactory.getLogger(MesaController.class);

    @Autowired
    MesaService mesaService;

    @GetMapping("/{id}")
    public Mesa getMesa(@PathVariable Long id) {
        LOGGER.info(String.format("Método get a la ruta /mesa/%d (obtener mesa)", id));
        return mesaService.getMesa(id);
    }

    @GetMapping
    public List<Mesa> getAllMesas() {
        LOGGER.info("Método get a la ruta /mesa (obtener todas las mesas)");
        return mesaService.getAllMesas();
    }

    @PostMapping
    public Mesa postMesa(@RequestBody Mesa mesa) {
        LOGGER.info("Método post a la ruta /mesa (crear mesa)");
        return mesaService.postMesa(mesa);
    }

    @PostMapping("/{idMesa}")
    public Mesa agregarSilla(@PathVariable Long idMesa, @RequestBody Silla silla){
        LOGGER.info(String.format("Método POST a la ruta /mesa/%d (agregar silla a la mesa)", idMesa));
        return mesaService.agregarSilla(silla, idMesa);
    }

    @PutMapping("/{id}")
    public Mesa updateMesa(@PathVariable Long id, @RequestBody Mesa mesaActualizada) {
        LOGGER.info(String.format("Método PUT a la ruta /mesa/%d (actualizar mesa)", id));
        return mesaService.updateMesa(id, mesaActualizada);
    }

    @PutMapping("/{idMesa}/{idSilla}")
    public Mesa updateSillaEnMesa(@PathVariable Long idMesa, @PathVariable Long idSilla, @RequestBody Silla silla){
        LOGGER.info(String.format("Método PUT a la ruta /mesa/%d/%d (actualizar silla en mesa)", idMesa, idSilla, silla));
        return mesaService.updateSillaEnMesa(idSilla, idMesa, silla);
    }

    @DeleteMapping("/{id}")
    public void deleteMesa(@PathVariable Long id) {
        LOGGER.info(String.format("Método DELETE a la ruta /mesa/%d (eliminar mesa)", id));
        mesaService.deleteMesa(id);
    }
}
