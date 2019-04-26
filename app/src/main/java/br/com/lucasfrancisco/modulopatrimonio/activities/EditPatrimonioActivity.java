package br.com.lucasfrancisco.modulopatrimonio.activities;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.lucasfrancisco.modulopatrimonio.R;
import br.com.lucasfrancisco.modulopatrimonio.adapters.ImagemAdapter;
import br.com.lucasfrancisco.modulopatrimonio.models.Empresa;
import br.com.lucasfrancisco.modulopatrimonio.models.Imagem;
import br.com.lucasfrancisco.modulopatrimonio.models.Patrimonio;
import br.com.lucasfrancisco.modulopatrimonio.models.Setor;

public class EditPatrimonioActivity extends AppCompatActivity {
    private static final int REQUEST_LOAD_IMAGE = 1;
    private static final int REQUEST_CAMERA_CODE = 3;

    private Spinner spnEmpresa, spnSetor;
    private EditText edtPlaqueta, edtTipo, edtMarca, edtModelo;
    private RecyclerView rcyImagens;
    private FloatingActionMenu famNovaImagem;
    private FloatingActionButton fabGaleria, fabNovaFoto;

    private boolean isEdit = false;

    private ArrayAdapter adapter;
    private List<Patrimonio> patrimonios;
    private Patrimonio patrimonio;
    private List<Imagem> imagens;
    private ImagemAdapter imagemAdapter;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    private Uri uriImagemCamera;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_patrimonio);

        getListPatrimonios();

        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_close);

        patrimonio = (Patrimonio) getIntent().getSerializableExtra("patrimonio");

        setTitle(patrimonio.getPlaqueta());

        spnEmpresa = (Spinner) findViewById(R.id.spnEmpresa);
        spnSetor = (Spinner) findViewById(R.id.spnSetor);
        edtPlaqueta = (EditText) findViewById(R.id.edtPlaqueta);
        edtTipo = (EditText) findViewById(R.id.edtTipo);
        edtMarca = (EditText) findViewById(R.id.edtMarca);
        edtModelo = (EditText) findViewById(R.id.edtModelo);
        rcyImagens = (RecyclerView) findViewById(R.id.rcyImagens);
        famNovaImagem = (FloatingActionMenu) findViewById(R.id.famNovaImagem);
        fabGaleria = (FloatingActionButton) findViewById(R.id.fabGaleria);
        fabNovaFoto = (FloatingActionButton) findViewById(R.id.fabNovaFoto);

        imagens = patrimonio.getImagens();
        for (int i = 0; i < imagens.size(); i++) {
            imagens.get(i).setUrlLocal(imagens.get(i).getUrlRemota());
        }

        imagemAdapter = new ImagemAdapter(imagens, getApplicationContext());

        rcyImagens.setHasFixedSize(true);
        rcyImagens.setLayoutManager(new LinearLayoutManager(this));
        rcyImagens.setAdapter(imagemAdapter);

        // Seta os valores vindos do PatrimonioFragment
        edtPlaqueta.setText(patrimonio.getPlaqueta());
        edtTipo.setText(patrimonio.getTipo());
        edtMarca.setText(patrimonio.getMarca());
        edtModelo.setText(patrimonio.getModelo());

        desativaEdicao();
        getSpinnerEmpresas();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_opcoes_objeto, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itEditar:
                ativaEdicao();
                return true;
            case R.id.itSalvar:
                atualizar();
                return true;
            case R.id.itExcluir:
                excluir();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_LOAD_IMAGE: // Abre a galeria
                    requestLoadImage(data);
                    break;
                case REQUEST_CAMERA_CODE: // Abre a câmera
                    requestCameraCode();
                    break;
            }
        }
    }

    public boolean ativaEdicao() {
        isEdit = true;
        spnEmpresa.setEnabled(true);
        spnSetor.setEnabled(true);
        edtPlaqueta.setEnabled(false); // A plaqueta nunca deve ser alterada
        edtTipo.setEnabled(true);
        edtMarca.setEnabled(true);
        edtModelo.setEnabled(true);
        famNovaImagem.setVisibility(View.VISIBLE);
        getFabNovaFoto();
        getFabGaleria();
        getItemTouch();

        return isEdit;
    }

    public boolean desativaEdicao() {
        isEdit = false;
        spnEmpresa.setEnabled(false);
        spnSetor.setEnabled(false);
        edtPlaqueta.setEnabled(false);
        edtTipo.setEnabled(false);
        edtMarca.setEnabled(false);
        edtModelo.setEnabled(false);
        famNovaImagem.setVisibility(View.INVISIBLE);

        return isEdit;
    }

    public void atualizar() {
        final String empresaVelha = (String) getIntent().getSerializableExtra("empresa");
        final String empresa = spnEmpresa.getSelectedItem().toString();
        final Setor setor = (Setor) spnSetor.getSelectedItem();
        final String plaqueta = edtPlaqueta.getText().toString();
        final String tipo = edtTipo.getText().toString();
        final String marca = edtMarca.getText().toString();
        final String modelo = edtModelo.getText().toString();
        final boolean isAtivo = true;
        final CollectionReference collectionReference = firebaseFirestore.collection("Empresas");

        if (tipo.trim().isEmpty() || marca.trim().isEmpty() || modelo.trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.dados_incompletos), Toast.LENGTH_SHORT).show();
        } else {
            Log.d("Imagem", "" + imagens.size());
            List<Imagem> novasImagens = new ArrayList<>();

            if (imagens.size() > 0) { // Salva patrimônio com imagem
                for (int i = 0; i < imagens.size(); i++) {
                    final String nomeArquivo = plaqueta + "_" + System.currentTimeMillis() + "." + getExtensaoArquivo(Uri.parse(imagens.get(i).getUrlLocal()));
                    StorageReference uploadReference = storageReference.child("Imagens/Patrimonios").child(nomeArquivo);
                    final int finalI = i;

                    if (!imagens.get(i).isEnviada()) { // Se a imagem já está salva no FirebaseStorage não será salva novamente
                        uploadReference.putFile(Uri.parse(imagens.get(i).getUrlLocal())).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                imagens.get(finalI).setEnviada(true);
                                imagens.get(finalI).setNome(nomeArquivo);
                                //
                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!uriTask.isSuccessful()) ;
                                imagens.get(finalI).setUrlRemota(uriTask.getResult().toString());
                                imagemAdapter.notifyDataSetChanged();

                                if (finalI == imagens.size() - 1) { // Se a imagem é o último item da lista salva o patrimônio
                                    collectionReference.document(empresaVelha).collection("Patrimonios").document(plaqueta).delete(); // Primeiro deleta para depois salvar. Isto evita ter dois patriônios iguais em mais de uma empresa
                                    patrimonio = new Patrimonio(tipo, marca, modelo, plaqueta, isAtivo, setor, imagens);
                                    collectionReference.document(empresa).collection("Patrimonios").document(plaqueta).set(patrimonio);
                                    Toast.makeText(getApplicationContext(), getString(R.string.patrimonio_atualizado), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        if (finalI == imagens.size() - 1) { // Se a imagem é o último item da lista salva o patrimônio
                            collectionReference.document(empresaVelha).collection("Patrimonios").document(plaqueta).delete(); // Primeiro deleta para depois salvar. Isto evita ter dois patriônios iguais em mais de uma empresa
                            patrimonio = new Patrimonio(tipo, marca, modelo, plaqueta, isAtivo, setor, imagens);
                            collectionReference.document(empresa).collection("Patrimonios").document(plaqueta).set(patrimonio);
                            Toast.makeText(getApplicationContext(), getString(R.string.patrimonio_atualizado), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                finish();
            } else { // Salva patrimônio sem imagem
                collectionReference.document(empresaVelha).collection("Patrimonios").document(plaqueta).delete(); // Primeiro deleta para depois salvar. Isto evita ter dois patriônios iguais em mais de uma empresa
                patrimonio = new Patrimonio(tipo, marca, modelo, plaqueta, isAtivo, setor, imagens);
                collectionReference.document(empresa).collection("Patrimonios").document(plaqueta).set(patrimonio);
                Toast.makeText(getApplicationContext(), getString(R.string.patrimonio_atualizado), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public void excluir() {
        String empresa = spnEmpresa.getSelectedItem().toString();
        final String plaqueta = edtPlaqueta.getText().toString();
        final CollectionReference collectionReference = firebaseFirestore.collection("Empresas").document(empresa).collection("Patrimonios");

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                DocumentReference documentReference = collectionReference.document(plaqueta);
                StorageReference storageReference;
                boolean isPatrimonio = false;
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Patrimonio patrimonio = documentSnapshot.toObject(Patrimonio.class);
                    if (patrimonio.getPlaqueta().equals(plaqueta)) {
                        for (int i = 0; i < patrimonio.getImagens().size(); i++){
                            storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(patrimonio.getImagens().get(i).getUrlRemota());
                            storageReference.delete();
                        }

                        isPatrimonio = true;
                        documentReference.delete();
                        Toast.makeText(getApplicationContext(), getString(R.string.patrimonio_excluido), Toast.LENGTH_SHORT).show();
                    }
                }
                if (!isPatrimonio) {
                    Toast.makeText(getApplicationContext(), getString(R.string.patrimonio_nao_encontrado), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getSpinnerEmpresas() {
        final ArrayList<Empresa> list = new ArrayList<>();
        final CollectionReference collectionReference = firebaseFirestore.collection("Empresas");

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    collectionReference.document(documentSnapshot.getId()).collection("Sobre").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                Empresa empresa = snapshot.toObject(Empresa.class);
                                list.add(empresa);
                            }

                            adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, list);
                            spnEmpresa.setAdapter(adapter);

                            // Seta a empresa do patrimônio no spnEmpresa como primeiro item
                            String empresa = (String) getIntent().getSerializableExtra("empresa");
                            for (int i = 0; i < spnEmpresa.getCount(); i++) {
                                if (spnEmpresa.getItemAtPosition(i).toString().equals(empresa)) {
                                    spnEmpresa.setSelection(i);
                                    break;
                                }
                            }

                            spnEmpresa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    getSpinnerSetores();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }
                    });
                }
            }
        });
    }

    public void getSpinnerSetores() {
        final ArrayList<Setor> list = new ArrayList<>();
        CollectionReference collectionReference = firebaseFirestore.collection("Empresas").document(spnEmpresa.getSelectedItem().toString()).collection("Setores");

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Setor setor = documentSnapshot.toObject(Setor.class);
                    list.add(setor);
                }

                adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, list);
                spnSetor.setAdapter(adapter);

                // Seta o setor do patrimônio no spnSetor como primeiro item
                String setor = (String) getIntent().getSerializableExtra("setor");
                for (int i = 0; i < spnSetor.getCount(); i++) {
                    if (spnSetor.getItemAtPosition(i).toString().equals(setor)) {
                        spnSetor.setSelection(i);
                        break;
                    }
                }

                spnSetor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });
    }

    public void getFabNovaFoto() {
        fabNovaFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "Nova imagem");
                values.put(MediaStore.Images.Media.DESCRIPTION, "Sua câmera");

                uriImagemCamera = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriImagemCamera);

                startActivityForResult(intent, REQUEST_CAMERA_CODE);
            }
        });
    }

    public void getFabGaleria() {
        fabGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Escolha imagem"), REQUEST_LOAD_IMAGE);
            }
        });
    }

    // Remove imagem da lista
    public void getItemTouch() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                imagemAdapter.excluir(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(rcyImagens);
    }

    public void getListPatrimonios() {
        final List<Patrimonio> list = new ArrayList<>();
        final CollectionReference collectionReference = firebaseFirestore.collection("Empresas");

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    collectionReference.document(documentSnapshot.getId()).collection("Patrimonios").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                Patrimonio patrimonio = snapshot.toObject(Patrimonio.class);
                                list.add(patrimonio);
                            }
                            patrimonios = list;
                        }
                    });
                }
            }
        });
    }

    private void requestLoadImage(Intent data) {
        if (data.getClipData() != null) { // Escolha multipla de arquivos
            int totalImagensSelecionadas = data.getClipData().getItemCount();
            for (int i = 0; i < totalImagensSelecionadas; i++) {
                Uri uri = data.getClipData().getItemAt(i).getUri();
                String nomeArquivo = getNomeArquivo(uri);
                Imagem imagem = new Imagem(nomeArquivo, uri.toString(), null, false);
                boolean isNaLista = false;
                // Verifica se já contém imagem com mesmo nome no RecyclerView
                for (int j = 0; j < imagens.size(); j++) {
                    if (imagens.get(j).getNome().equals(nomeArquivo)) {
                        isNaLista = true;
                        Toast.makeText(getApplicationContext(), getString(R.string.imagem_ja_esta_na_lista), Toast.LENGTH_LONG).show();
                        break;
                    }
                }
                // Se não hover imagem com o mesmo nome no RecyclerView, será adicionado a nova imagem
                if (!isNaLista) {
                    imagens.add(imagem);
                }
                imagemAdapter.notifyDataSetChanged();
            }
        } else if (data.getData() != null) { // Escolha simples de arquivos
            Uri uri = data.getData();
            String nomeArquivo = getNomeArquivo(uri);
            Imagem imagem = new Imagem(nomeArquivo, uri.toString(), null, false);
            boolean isNaLista = false;
            // Verifica se já contém imagem com mesmo nome no RecyclerView
            for (int j = 0; j < imagens.size(); j++) {
                if (imagens.get(j).getNome().equals(nomeArquivo)) {
                    isNaLista = true;
                    Toast.makeText(getApplicationContext(), getString(R.string.imagem_ja_esta_na_lista), Toast.LENGTH_LONG).show();
                    break;
                }
            }
            // Se não hover imagem com o mesmo nome no RecyclerView, será adicionado a nova imagem
            if (!isNaLista) {
                imagens.add(imagem);
            }
            imagemAdapter.notifyDataSetChanged();
        }
    }

    private void requestCameraCode() {
        String nomeArquivo = getNomeArquivo(uriImagemCamera);
        Imagem imagem = new Imagem(nomeArquivo, uriImagemCamera.toString(), null, false);
        imagens.add(imagem);
        imagemAdapter.notifyDataSetChanged();
    }

    // Retorna o nome de um arquivo selecionado no gerenciador de arquivos
    public String getNomeArquivo(Uri uri) {
        String resultado = null;

        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    resultado = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }

        if (resultado == null) {
            resultado = uri.getPath();
            int corte = resultado.lastIndexOf('/');
            if (corte != -1) {
                resultado = resultado.substring(corte + 1);
            }
        }
        return resultado;
    }

    private String getExtensaoArquivo(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
}


/*
public void atualizar() {
        String empresaVelha = (String) getIntent().getSerializableExtra("empresa");
        String empresa = spnEmpresa.getSelectedItem().toString();
        Setor setor = (Setor) spnSetor.getSelectedItem();
        String plaqueta = edtPlaqueta.getText().toString();
        String tipo = edtTipo.getText().toString();
        String marca = edtMarca.getText().toString();
        String modelo = edtModelo.getText().toString();
        boolean isAtivo = true;
        CollectionReference collectionReference = firebaseFirestore.collection("Empresas");
        Patrimonio patrimonio;

        if (tipo.trim().isEmpty() || marca.trim().isEmpty() || modelo.trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.dados_incompletos), Toast.LENGTH_SHORT).show();
        } else {
            collectionReference.document(empresaVelha).collection("Patrimonios").document(plaqueta).delete(); // Primeiro deleta para depois salvar. Isto evita ter dois patriônios iguais em mais de uma empresa
            patrimonio = new Patrimonio(tipo, marca, modelo, plaqueta, isAtivo, setor, imagens);
            collectionReference.document(empresa).collection("Patrimonios").document(plaqueta).set(patrimonio);
            Toast.makeText(getApplicationContext(), getString(R.string.patrimonio_atualizado), Toast.LENGTH_SHORT).show();
            finish();
        }
    }


     public void atualizar() {
        final String empresaVelha = (String) getIntent().getSerializableExtra("empresa");
        final String empresa = spnEmpresa.getSelectedItem().toString();
        final Setor setor = (Setor) spnSetor.getSelectedItem();
        final String plaqueta = edtPlaqueta.getText().toString();
        final String tipo = edtTipo.getText().toString();
        final String marca = edtMarca.getText().toString();
        final String modelo = edtModelo.getText().toString();
        final boolean isAtivo = true;
        final CollectionReference collectionReference = firebaseFirestore.collection("Empresas");

        if (tipo.trim().isEmpty() || marca.trim().isEmpty() || modelo.trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.dados_incompletos), Toast.LENGTH_SHORT).show();
        } else {
            Log.d("Imagem", "" + imagens.size());
            List<Imagem> novasImagens = new ArrayList<>();

            if (imagens.size() > 0) { // Salva patrimônio com imagem
                for (int i = 0; i < imagens.size(); i++) {
                    final String nomeArquivo = plaqueta + "_" + System.currentTimeMillis() + "." + getExtensaoArquivo(Uri.parse(imagens.get(i).getUrlLocal()));
                    StorageReference uploadReference = storageReference.child("Imagens/Patrimonios").child(nomeArquivo);
                    final int finalI = i;

                    if (!imagens.get(i).isEnviada()) { // Arrumar esta condição
                        uploadReference.putFile(Uri.parse(imagens.get(i).getUrlLocal())).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                imagens.get(finalI).setEnviada(true);
                                imagens.get(finalI).setNome(nomeArquivo);
                                //
                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!uriTask.isSuccessful()) ;
                                imagens.get(finalI).setUrlRemota(uriTask.getResult().toString());
                                imagemAdapter.notifyDataSetChanged();

                                if (finalI == imagens.size() - 1) {
                                    collectionReference.document(empresaVelha).collection("Patrimonios").document(plaqueta).delete(); // Primeiro deleta para depois salvar. Isto evita ter dois patriônios iguais em mais de uma empresa
                                    patrimonio = new Patrimonio(tipo, marca, modelo, plaqueta, isAtivo, setor, imagens);
                                    collectionReference.document(empresa).collection("Patrimonios").document(plaqueta).set(patrimonio);
                                    Toast.makeText(getApplicationContext(), getString(R.string.patrimonio_atualizado), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
                finish();
            } else { // Salva patrimônio sem imagem
                collectionReference.document(empresaVelha).collection("Patrimonios").document(plaqueta).delete(); // Primeiro deleta para depois salvar. Isto evita ter dois patriônios iguais em mais de uma empresa
                patrimonio = new Patrimonio(tipo, marca, modelo, plaqueta, isAtivo, setor, imagens);
                collectionReference.document(empresa).collection("Patrimonios").document(plaqueta).set(patrimonio);
                Toast.makeText(getApplicationContext(), getString(R.string.patrimonio_atualizado), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
 */