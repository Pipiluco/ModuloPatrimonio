package br.com.lucasfrancisco.modulopatrimonio.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import br.com.lucasfrancisco.modulopatrimonio.R;
import br.com.lucasfrancisco.modulopatrimonio.models.Empresa;
import br.com.lucasfrancisco.modulopatrimonio.models.Endereco;
import br.com.lucasfrancisco.modulopatrimonio.models.Objeto;
import br.com.lucasfrancisco.modulopatrimonio.models.Patrimonio;
import br.com.lucasfrancisco.modulopatrimonio.models.Setor;
import br.com.lucasfrancisco.modulopatrimonio.models.Usuario;

public class NovoObjetoActivity extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = firebaseFirestore.collection("Empresas");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        salvar();
    }

    public void salvar() {
        Objeto objeto = new Objeto();
        Setor setor = new Setor();
        Usuario usuario = new Usuario();
        Empresa empresa = new Empresa();
        Endereco endereco = new Endereco();
        Patrimonio patrimonio = new Patrimonio();

        //
        objeto.setTipo("Computador");
        objeto.setMarca("Dell");
        objeto.setModelo("Vostro 3460");
        objeto.setCor("Preto");

        //
        endereco.setRua("Alexandre Osvald Tarnowski");
        endereco.setNumero(34);
        endereco.setCEP("89130-000");
        endereco.setBairro("Warnow");
        endereco.setCidade("Indaial");
        endereco.setEstado("Santa Catarina");
        endereco.setPais("Brasil");

        //
        empresa.setNome("Serviço Nacional de Aprendizagem Industrial");
        empresa.setFantasia("SENAI");
        empresa.setCodigo("4081");
        empresa.setCNPJ("452.562.65/5445.55");

        //

        setor.setBloco("Bloco B");
        setor.setSetor("Sala de aula");
        setor.setSala("B-02");

        //
        usuario.setNome("Lucas");
        usuario.setSobrenome("Francisco");
        usuario.setCargo("Técnico de TI");
        usuario.setCPF("076.780.42444");
        usuario.setIdade(30);

        //
        patrimonio.setPlaqueta("270234");

        collectionReference.document(empresa.getCodigo()).collection("Patrimonios").document(patrimonio.getPlaqueta()).set(patrimonio);
        collectionReference.document(empresa.getCodigo()).collection("Endereco").document(endereco.getCEP()).set(endereco);
        collectionReference.document(empresa.getCodigo()).collection("Setores").document(setor.getSala()).set(setor);
        collectionReference.document(empresa.getCodigo()).collection("Usuarios").document(usuario.getCPF()).set(usuario);
    }
}
