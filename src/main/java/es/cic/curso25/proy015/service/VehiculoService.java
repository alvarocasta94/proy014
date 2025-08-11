package es.cic.curso25.proy015.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.cic.curso25.proy015.exceptions.NotFoundException;
import es.cic.curso25.proy015.model.Vehiculo;
import es.cic.curso25.proy015.model.Multa;
import es.cic.curso25.proy015.model.Plaza;
import es.cic.curso25.proy015.repository.MultaRepository;
import es.cic.curso25.proy015.repository.VehiculoRepository;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class VehiculoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VehiculoService.class);

    @Autowired
    VehiculoRepository vehiculoRepository;

    @Autowired
    MultaRepository multaRepository;

    // CRUD VEHÍCULO

    // CREATE
    @Transactional
    public Vehiculo crearVehiculo(Vehiculo vehiculo) {
        LOGGER.info("Creando un nuevo vehículo");
        return vehiculoRepository.save(vehiculo);
    }

    // READ
    @Transactional(readOnly = true)
    public Vehiculo getVehiculo(Long id) {
        LOGGER.info(String.format("Buscando el coche con id %d", id));
        Optional<Vehiculo> vehiculo = vehiculoRepository.findById(id);
        if (vehiculo.isEmpty()) {
            throw new NotFoundException(String.format("No se ha encontrado ningún vehiculo con id %d", id));
        }
        return vehiculo.get();
    }

    @Transactional(readOnly = true)
    public List<Vehiculo> getAllVehiculos() {
        LOGGER.info("Obteniendo todos las vehículos");
        return vehiculoRepository.findAll();
    }

    // UPDATE
    @Transactional
    public Vehiculo updatevehiculo(Long id, Vehiculo vehiculo) {
        LOGGER.info(String.format("Actualizando el vehículo con id %d", id));
        // Comprobamos que exista un vehículo con ese id
        Vehiculo vehiculoEnBD = this.getVehiculo(id);

        vehiculoEnBD.setColor(vehiculo.getColor());
        vehiculoEnBD.setMarca(vehiculo.getMarca());
        vehiculoEnBD.setModelo(vehiculo.getModelo());
        vehiculoEnBD.setTipo(vehiculo.getTipo());

        return vehiculoRepository.save(vehiculoEnBD);
    }

    @Transactional
    public Vehiculo aparcarVehiculo(Long idVehiculo, Plaza plaza) {
        LOGGER.info(String.format("Aparcando el vehículo con id %d en plaza con id %d", idVehiculo, plaza.getId()));
        // Comprobamos que exista un vehículo con ese id
        Vehiculo vehiculoEnBD = this.getVehiculo(idVehiculo);

        vehiculoEnBD.setPlazaOcupada(plaza);
        plaza.ocuparConVehiculo(vehiculoEnBD);

        return vehiculoRepository.save(vehiculoEnBD);
    }

    @Transactional
    public Vehiculo desaparcarVehiculo(Long idVehiculo) {
        LOGGER.info(String.format("Desaparcando el vehículo con id %d", idVehiculo));
        Vehiculo vehiculoEnBD = this.getVehiculo(idVehiculo);

        Plaza plaza = vehiculoEnBD.getPlazaOcupada();
        if (plaza != null) {
            plaza.desocuparVehiculo(); // Limpia la plaza ocupada y rompe la relación bidireccional
            vehiculoEnBD.setPlazaOcupada(null);
        }

        return vehiculoRepository.save(vehiculoEnBD);
    }

    // DELETE
    @Transactional
    public void deleteVehiculo(Long id) {
        LOGGER.info(String.format("Eliminando el vehículo con id %d", id));
        Vehiculo vehiculo = this.getVehiculo(id);

        if (vehiculo.getPlazaAsignada() != null) {
            vehiculo.getPlazaAsignada().quitarVehiculo(vehiculo); // rompe relación plaza asignada
        }

        if (vehiculo.getPlazaOcupada() != null) {
            vehiculo.getPlazaOcupada().ocuparConVehiculo(null); // rompe relación plaza ocupada
        }

        vehiculoRepository.delete(vehiculo);
    }

    // CRUD MULTA

    // CREATE
    @Transactional
    public Multa agregarMulta(Long id, Multa multa) {
        LOGGER.info(String.format("Agregando una nueva multa al vehículo con id %d", id));
        Vehiculo vehiculoModificado = this.getVehiculo(id);
        vehiculoModificado.addMulta(multa);
        multa.setVehiculo(vehiculoModificado);

        // Guardamos el vehículo, que por cascade guarda la multa
        vehiculoRepository.save(vehiculoModificado);

        // Recuperamos la multa guardada desde la base de datos para tenerla actualizada
        return multaRepository.findById(multa.getId())
                .orElseThrow(() -> new RuntimeException("Error al recuperar la multa guardada"));
    }

    // READ
    @Transactional(readOnly = true)
    public Multa getMultaById(Long idVehiculo, Long idMulta) {
        LOGGER.info(String.format("Buscando la multa con id %d en el vehículo con id %d", idMulta, idVehiculo));
        Multa multa = multaRepository.findById(idMulta)
                .orElseThrow(() -> new NotFoundException(
                        String.format("No se ha encontrado ninguna multa con id %d", idMulta)));

        if (multa.getVehiculo() == null || !multa.getVehiculo().getId().equals(idVehiculo)) {
            throw new NotFoundException(
                    String.format("La multa con id %d no pertenece al vehículo con id %d", idMulta, idVehiculo));
        }
        return multa;
    }

    @Transactional(readOnly = true)
    public List<Multa> getMultasByVehiculo(Long id) {
        LOGGER.info(String.format("Obteniendo todas las multas del vehículo con id %d", id));
        Vehiculo vehiculo = vehiculoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        String.format("No se ha encontrado ningún vehículo con id %d", id)));
        return vehiculo.getMultas();
    }

    // UPDATE
    @Transactional
    public Multa updateMulta(Long idVehiculo, Long idMulta, Multa multa) {
        LOGGER.info(String.format("Actualizando la multa con id %d del vehículo con id %d", idMulta, idVehiculo));
        // Comprobamos que exista una multa con ese idMulta en el vehículo con
        // idVehiculo
        Multa multaEnBD = this.getMultaById(idVehiculo, idMulta);
        multaEnBD.setFecha(multa.getFecha());
        multaEnBD.setCuantia(multa.getCuantia());
        return multaRepository.save(multaEnBD);
    }

    // DELETE
    @Transactional
    public void eliminarMulta(Long idVehiculo, Long idMulta) {
        LOGGER.info(String.format("Eliminando la multa con id %d del vehículo con id %d", idMulta, idVehiculo));
        Vehiculo vehiculoModificado = this.getVehiculo(idVehiculo);
        Multa multaAEliminar = this.getMultaById(idVehiculo, idMulta);
        vehiculoModificado.removeMulta(multaAEliminar);

        // Guardamos el vehículo, que por cascade guarda la multa
        vehiculoRepository.save(vehiculoModificado);
    }

}
