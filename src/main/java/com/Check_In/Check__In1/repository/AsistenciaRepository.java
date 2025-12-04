package com.Check_In.Check__In1.repository;

import com.Check_In.Check__In1.entity.Asistencia;
import com.Check_In.Check__In1.entity.EstadoAsistencia;
import com.Check_In.Check__In1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {
    List<Asistencia> findByUser(User user);
    List<Asistencia> findByFecha(LocalDate fecha);

    boolean existsByUserAndFecha(User user, LocalDate fecha);

    long countByUserIdAndEstado(Long userId, EstadoAsistencia estado);
    List<Asistencia> findByUserAndEstado(User user, EstadoAsistencia estado);

    Optional<Asistencia> findByUserAndFecha(User user, LocalDate fecha);

    @Query("SELECT a FROM Asistencia a JOIN FETCH a.user")
    List<Asistencia> findAllConUsuarios();

    @Query("SELECT a.estado, COUNT(a) FROM Asistencia a GROUP BY a.estado")
    List<Object[]> contarPorEstado();




}
