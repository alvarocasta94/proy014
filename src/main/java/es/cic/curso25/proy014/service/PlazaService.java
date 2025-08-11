package es.cic.curso25.proy015.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import es.cic.curso25.proy015.exceptions.NotFoundException;
import es.cic.curso25.proy015.model.Plaza;
import es.cic.curso25.proy015.model.Vehiculo;
import es.cic.curso25.proy015.repository.PlazaRepository;
import es.cic.curso25.proy015.repository.VehiculoRepository;

public class PlazaService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlazaService.class);

    @Autowired
    VehiculoRepository vehiculoRepository;

    @Autowired
    PlazaRepository plazaRepository;

    // CRUD PLAZA

    // READ
    @Transactional(readOnly = true)
    public Plaza getPlaza(Long id) {
        LOGGER.info(String.format("Buscando la plaza con id %d", id));
        Optional<Plaza> plaza = plazaRepository.findById(id);
        if (plaza.isEmpty()) {
            throw new NotFoundException(String.format("No se ha encontrado ninguna plaza con id %d", id));
        }
        return plaza.get();
    }

    @Transactional(readOnly = true)
    public List<Plaza> getAllPlazas() {
        LOGGER.info("Obteniendo todas las plazas");
        return plazaRepository.findAll();
    }

    // UPDATE
    @Transactional
    public Plaza updatePlaza(Long id, Plaza plaza) {
        LOGGER.info(String.format("Actualizando la plaza con id %d", id));
        // Comprobamos que exista un Plaza con ese id
        Plaza plazaEnBD = this.getPlaza(id);

        // Regla: no se puede poner como no disponible si está ocupada
        if (!plaza.isDisponible() && plazaEnBD.isOcupado()) {
            throw new IllegalArgumentException("No se puede marcar la plaza como no disponible si está ocupada");
        }

        plazaEnBD.setDisponible(plaza.isDisponible());

        plazaEnBD.setOcupado(plaza.isOcupado());

        plazaEnBD.setVehiculo(plaza.getVehiculo());

        return plazaRepository.save(plazaEnBD);
    }

    // Asignar Vehículo a Plaza
    @Transactional
    public Plaza asignarVehiculo(Long plazaId, Long vehiculoId) {
        LOGGER.info(String.format("Asignando vehículo %d a plaza %d", vehiculoId, plazaId));

        // Buscar plaza
        Plaza plaza = plazaRepository.findById(plazaId)
                .orElseThrow(() -> new NotFoundException(String.format("No se encontró la plaza con id %d", plazaId)));

        // Buscar vehículo
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(
                        () -> new NotFoundException(String.format("No se encontró el vehículo con id %d", vehiculoId)));

        // Si la plaza ya tiene un vehículo asignado, desvincularlo
        if (plaza.getVehiculo() != null) {
            Vehiculo vehiculoAnterior = plaza.getVehiculo();
            vehiculoAnterior.setPlaza(null);
        }

        // Si el vehículo ya estaba asignado a otra plaza, desvincular esa plaza
        if (vehiculo.getPlaza() != null && !vehiculo.getPlaza().equals(plaza)) {
            Plaza plazaAnterior = vehiculo.getPlaza();
            plazaAnterior.setVehiculo(null);
        }

        // Asignar vehículo a plaza y plaza a vehículo (manteniendo la relación
        // bidireccional)
        plaza.setVehiculo(vehiculo);
        vehiculo.setPlaza(plaza);

        // Guardar ambos para persistir la relación
        plazaRepository.save(plaza);
        vehiculoRepository.save(vehiculo);

        return plaza;
    }

}
