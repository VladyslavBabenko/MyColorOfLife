package com.github.vladyslavbabenko.mycoloroflife.service.implementation;

import com.github.vladyslavbabenko.mycoloroflife.entity.ActivationCode;
import com.github.vladyslavbabenko.mycoloroflife.entity.CourseTitle;
import com.github.vladyslavbabenko.mycoloroflife.entity.User;
import com.github.vladyslavbabenko.mycoloroflife.repository.ActivationCodeRepository;
import com.github.vladyslavbabenko.mycoloroflife.service.ActivationCodeService;
import org.apache.commons.text.RandomStringGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link ActivationCodeService}.
 */

@Transactional
@Service
public class ActivationCodeServiceImpl implements ActivationCodeService {

    private final ActivationCodeRepository codeRepository;

    @Autowired
    public ActivationCodeServiceImpl(ActivationCodeRepository codeRepository) {
        this.codeRepository = codeRepository;
    }

    @Override
    public List<ActivationCode> getAll() {
        List<ActivationCode> activationCodes = codeRepository.findAll();
        activationCodes.sort(Comparator.comparingLong(ActivationCode::getId).reversed());
        return activationCodes;
    }

    @Override
    public List<ActivationCode> findAllByUser(User user) {
        return codeRepository.findAllByUser(user).orElse(new ArrayList<>());
    }

    @Override
    public Optional<ActivationCode> findById(Integer codeId) {
        return codeRepository.findById(codeId);
    }

    @Override
    public Optional<ActivationCode> findByCode(String code) {
        return codeRepository.findByCode(code);
    }

    @Override
    public List<ActivationCode> findAllByUserAndCourseTitle(User user, CourseTitle courseTitle) {
        return codeRepository.findAllByUserAndCourseTitle(user, courseTitle).orElse(new ArrayList<>());
    }

    @Override
    public boolean existsByCode(String code) {
        return codeRepository.existsByCode(code);
    }

    @Override
    public boolean existsByUser(User user) {
        return codeRepository.existsByUser(user);
    }

    @Override
    public ActivationCode createCode(CourseTitle courseTitle, User user) {
        ActivationCode code = ActivationCode.builder().courseTitle(courseTitle).user(user).code(generateCode()).build();
        save(code);
        return code;
    }

    @Override
    public boolean save(ActivationCode codeToSave) {
        if (existsByCode(codeToSave.getCode())) {
            return false;
        }

        codeRepository.save(codeToSave);
        return true;
    }

    @Override
    public boolean deleteAllByUser(User user) {
        if (existsByUser(user)) {
            codeRepository.deleteAllByUser(user);
            return true;
        }

        return false;
    }

    @Override
    public boolean deleteByCode(String code) {
        if (existsByCode(code)) {
            codeRepository.deleteByCode(code);
            return true;
        }

        return false;
    }

    @Override
    public boolean update(ActivationCode updatedCode) {
        Optional<ActivationCode> optionalActivationCode = codeRepository.findById(updatedCode.getId());

        if (optionalActivationCode.isEmpty()) {
            return false;
        } else {
            ActivationCode activationCodeToUpdate = optionalActivationCode.get();

            activationCodeToUpdate.setCode(updatedCode.getCode());
            activationCodeToUpdate.setCourseTitle(updatedCode.getCourseTitle());
            activationCodeToUpdate.setUser(updatedCode.getUser());

            codeRepository.save(activationCodeToUpdate);
            return true;
        }
    }

    private String generateCode() {
        char[][] chars = {{'a', 'z'}, {'A', 'Z'}, {'0', '9'}};
        return new RandomStringGenerator.Builder().withinRange(chars).build().generate(15);
    }
}