package lemonsoft.senac.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lemonsoft.senac.model.Papel;
import lemonsoft.senac.repository.PapelRepository;

@Component
public class CarregaDados implements CommandLineRunner {

    @Autowired
    PapelRepository papelRepository;

    @Override
    public void run(String... args) throws Exception {
        
        String[] papeis = {"ADMIN", "ESTOQUISTA"};

        for( String papelString : papeis) {
            Papel papel = papelRepository.findByPapel(papelString);
            if (papel == null) {
                papel = new Papel(papelString);
                papelRepository.save(papel);
            }
        }
    }
    
}
