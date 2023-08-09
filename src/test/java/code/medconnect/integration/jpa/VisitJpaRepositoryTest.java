package code.medconnect.integration.jpa;

import code.medconnect.fixtures.EntityFixtures;
import code.medconnect.infrastructure.database.entity.DoctorAvailabilityEntity;
import code.medconnect.infrastructure.database.entity.DoctorEntity;
import code.medconnect.infrastructure.database.entity.PatientEntity;
import code.medconnect.infrastructure.database.entity.VisitEntity;
import code.medconnect.infrastructure.database.repository.jpa.DoctorAvailabilityJpaRepository;
import code.medconnect.infrastructure.database.repository.jpa.DoctorJpaRepository;
import code.medconnect.infrastructure.database.repository.jpa.PatientJpaRepository;
import code.medconnect.infrastructure.database.repository.jpa.VisitJpaRepository;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.yaml")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class VisitJpaRepositoryTest {

    VisitJpaRepository visitJpaRepository;
    PatientJpaRepository patientJpaRepository;
    DoctorJpaRepository doctorJpaRepository;

    @Test
    void shouldSaveVisitCorrectly() {
        //given
        PatientEntity patient = patientJpaRepository.saveAndFlush(EntityFixtures.somePatient1());
        String pesel = patient.getPesel();
        DoctorEntity doctor = doctorJpaRepository.saveAndFlush(EntityFixtures.someDoctor1());
        VisitEntity visit = EntityFixtures.someVisit().withDoctor(doctor).withPatient(patient);
        visitJpaRepository.saveAndFlush(visit);

        //when
        List<VisitEntity> visits = visitJpaRepository.findVisitByPatientPesel(pesel);

        //then
        assertThat(visits).hasSize(1);
    }

    @Test
    void shouldCancelVisitCorrectly() {
        //given
        PatientEntity patient = patientJpaRepository.saveAndFlush(EntityFixtures.somePatient1());
        String pesel = patient.getPesel();
        DoctorEntity doctor = doctorJpaRepository.saveAndFlush(EntityFixtures.someDoctor1());
        VisitEntity visit = EntityFixtures.someVisit().withDoctor(doctor).withPatient(patient);
        VisitEntity savedVisit = visitJpaRepository.saveAndFlush(visit);


        //when
        Integer visitId = savedVisit.getVisitId();
        visitJpaRepository.cancelVisitById(visitId);

        //then
        VisitEntity cancelledVisit = visitJpaRepository.findById(visitId).orElse(null);
        assertNotNull(cancelledVisit);
        assertTrue(cancelledVisit.isCanceled());
    }
}
