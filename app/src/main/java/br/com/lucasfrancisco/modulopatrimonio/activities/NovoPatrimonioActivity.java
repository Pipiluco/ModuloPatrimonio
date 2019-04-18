package br.com.lucasfrancisco.modulopatrimonio.activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import br.com.lucasfrancisco.modulopatrimonio.R;
import br.com.lucasfrancisco.modulopatrimonio.adapters.NovaImagemAdapter;
import br.com.lucasfrancisco.modulopatrimonio.models.Imagem;
import br.com.lucasfrancisco.modulopatrimonio.models.Patrimonio;
import br.com.lucasfrancisco.modulopatrimonio.models.Setor;

public class NovoPatrimonioActivity extends AppCompatActivity {
    private static final int RESULT_LOAD_IMAGE = 1;

    private Spinner spnEmpresa, spnSetor;
    private EditText edtPlaqueta, edtTipo, edtMarca, edtModelo;
    //private ProgressBar pbUpload;
    //private ImageView imvImagem;
    private RecyclerView rcyImagens;
    private FloatingActionButton fabNovaEmpresa, fabNovoSetor, fabNovaImagem;
    private Toolbar tbrBottomMain;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    // private StorageTask uploadTask;

    // private Uri uriImagem;

    private ArrayAdapter adapter;
    private NovaImagemAdapter imagemAdapter;
    private ArrayList<String> listEmpresas;
    //private ArrayList<String> listSetores;
    private ArrayList<String> listPatrimonios;
    private List<String> listImagensEscolhidas;
    private List<Boolean> listImagensEnviadas;
    private List<Uri> listUriImagens;
    private int totalImagensList = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_patrimonio);

        getListPatrimonios();

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle(getString(R.string.novo_patrimonio));

        spnEmpresa = (Spinner) findViewById(R.id.spnEmpresa);
        spnSetor = (Spinner) findViewById(R.id.spnSetor);
        edtPlaqueta = (EditText) findViewById(R.id.edtPlaqueta);
        edtTipo = (EditText) findViewById(R.id.edtTipo);
        edtMarca = (EditText) findViewById(R.id.edtMarca);
        edtModelo = (EditText) findViewById(R.id.edtModelo);
        // pbUpload = (ProgressBar) findViewById(R.id.pbUpload);
        // imvImagem = (ImageView) findViewById(R.id.imvImagem);
        rcyImagens = (RecyclerView) findViewById(R.id.rcyImagens);
        fabNovaEmpresa = (FloatingActionButton) findViewById(R.id.fabNovaEmpresa);
        fabNovoSetor = (FloatingActionButton) findViewById(R.id.fabNovoSetor);
        fabNovaImagem = (FloatingActionButton) findViewById(R.id.fabNovaImagem);
        tbrBottomMain = (Toolbar) findViewById(R.id.incTbrBottom);

        listImagensEscolhidas = new ArrayList<>();
        listImagensEnviadas = new ArrayList<>();
        listUriImagens = new ArrayList<>();
        imagemAdapter = new NovaImagemAdapter(listImagensEscolhidas, listImagensEnviadas, listUriImagens, getApplicationContext());

        rcyImagens.setLayoutManager(new LinearLayoutManager(this));
        rcyImagens.setHasFixedSize(true);
        rcyImagens.setAdapter(imagemAdapter);

        getSpinnerEmpresas();
        getFabNovaEmpresa();
        getFabNovoSetor();
        getFabNovaImagem();
        getTbrBottomMain();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        storageReference = FirebaseStorage.getInstance().getReference();

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                int totalImagensSelecionadas = data.getClipData().getItemCount();
                for (int i = 0; i < totalImagensSelecionadas; i++) {
                    Uri uri = data.getClipData().getItemAt(i).getUri();
                    String nomeArquivo = getNomeArquivo(uri);

                    listImagensEscolhidas.add(nomeArquivo);
                    listImagensEnviadas.add(false);
                    listUriImagens.add(uri);
                    imagemAdapter.notifyDataSetChanged();

                    StorageReference uploadReference = storageReference.child("Imagens/Patrimonios").child(nomeArquivo);

                    final int finalI = i;
                    uploadReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            listImagensEnviadas.remove(finalI);
                            listImagensEnviadas.add(finalI, true);
                            imagemAdapter.notifyDataSetChanged();
                        }
                    });
                }
            } else if (data.getData() != null) {
                Uri uri = data.getData();
                String nomeArquivo = getNomeArquivo(uri);

                listImagensEscolhidas.add(nomeArquivo);
                listImagensEnviadas.add(false);
                listUriImagens.add(uri);
                imagemAdapter.notifyDataSetChanged();

                StorageReference uploadReference = storageReference.child("Imagens/Patrimonios").child(nomeArquivo);

                uploadReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        listImagensEnviadas.remove(totalImagensList);
                        listImagensEnviadas.add(totalImagensList, true);
                        imagemAdapter.notifyDataSetChanged();
                        totalImagensList = totalImagensList + 1;
                    }
                });
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_salvar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itSalvar:
                salvar();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void salvar() { // Estável OK
        if (spnEmpresa.getSelectedItem() == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.necessario_empresa), Toast.LENGTH_SHORT).show();
            return;
        }

        if (spnSetor.getSelectedItem() == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.necessario_setor), Toast.LENGTH_SHORT).show();
            return;
        }

        Setor setor = (Setor) spnSetor.getSelectedItem();
        String nomeEmpresa = spnEmpresa.getSelectedItem().toString();
        String plaqueta = edtPlaqueta.getText().toString();
        String tipo = edtTipo.getText().toString();
        String marca = edtMarca.getText().toString();
        String modelo = edtModelo.getText().toString();
        Boolean isPatrimonio = false;
        CollectionReference collectionReference = firebaseFirestore.collection("Empresas");
        Patrimonio patrimonio;
        //
        ArrayList<Imagem> imagens = new ArrayList<>();
        imagens.add(new Imagem("Gato", "www.gato.com.br"));
        imagens.add(new Imagem("Boi", "www.boi.com.br"));
        imagens.add(new Imagem("Cachorro", "www.cachorro.com.br"));
        //


        for (int i = 0; i < listPatrimonios.size(); i++) {
            // Log.d("PATRIMÔNIO", "" + listPatrimonios.get(i));
            if (plaqueta.equals(listPatrimonios.get(i))) {
                isPatrimonio = true;
                Toast.makeText(getApplicationContext(), getString(R.string.patrimonio_ja_existe) + " (" + plaqueta + ")", Toast.LENGTH_SHORT).show();
            }
        }

        if (!isPatrimonio) {
            // Log.d("NÃO EXISTE", plaqueta);
            if (plaqueta.trim().isEmpty() || tipo.trim().isEmpty() || marca.trim().isEmpty() || modelo.trim().isEmpty()) {
                Toast.makeText(getApplicationContext(), getString(R.string.dados_incompletos), Toast.LENGTH_SHORT).show();
            } else {
                patrimonio = new Patrimonio(tipo, marca, modelo, plaqueta, true, setor, imagens);
                collectionReference.document(nomeEmpresa).collection("Patrimonios").document(plaqueta).set(patrimonio);
                Toast.makeText(getApplicationContext(), getString(R.string.patrimonio_salvo), Toast.LENGTH_SHORT).show();
                getListPatrimonios();
                isPatrimonio = true;

                // Limpa campos de texto
                edtPlaqueta.setText("");
                edtTipo.setText("");
                edtMarca.setText("");
                edtModelo.setText("");
            }
        }
    }

    public void getListPatrimonios() {
        final ArrayList<String> list = new ArrayList<>();
        final CollectionReference collectionReference = firebaseFirestore.collection("Empresas");

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    collectionReference.document(documentSnapshot.getId()).collection("Patrimonios").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                list.add(snapshot.getId());
                            }
                            listPatrimonios = list;
                        }
                    });
                }
            }
        });
    }

    public void getSpinnerEmpresas() {
        final ArrayList<String> list = new ArrayList<>();
        CollectionReference collectionReference = firebaseFirestore.collection("Empresas");

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    list.add(documentSnapshot.getId());
                }

                listEmpresas = list;
                adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, listEmpresas);
                spnEmpresa.setAdapter(adapter);

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

                //listSetores = list;
                adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, list);
                spnSetor.setAdapter(adapter);

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

    public void getFabNovaEmpresa() {
        fabNovaEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NovaEmpresaActivity.class);
                startActivity(intent);
            }
        });
    }

    public void getFabNovoSetor() {
        fabNovoSetor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NovoSetorActivity.class);
                startActivity(intent);
            }
        });
    }

    public void getFabNovaImagem() {
        fabNovaImagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Escolha imagem"), RESULT_LOAD_IMAGE);
            }
        });
    }

    // Toolbar inferior
    public void getTbrBottomMain() {
        tbrBottomMain.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = null;

                switch (menuItem.getItemId()) {
                    case R.id.itPatrimonio:
                        intent = new Intent(getApplicationContext(), NovoPatrimonioActivity.class);
                        break;
                    case R.id.itEmpresa:
                        intent = new Intent(getApplicationContext(), NovaEmpresaActivity.class);
                        break;
                    case R.id.itEndereco:
                        intent = new Intent(getApplicationContext(), NovoEnderecoActivity.class);
                        break;
                }
                startActivity(intent);
                return true;
            }
        });
        tbrBottomMain.inflateMenu(R.menu.menu_toolbar_main);
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
}

// Upload de imagem
/*
@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriImagem = data.getData();
            Picasso.with(this).load(uriImagem).into(imvImagem);
        }
    }

    private void uploadImagem() {
        final CollectionReference collectionReference = firebaseFirestore.collection("Uploads");
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        if (uriImagem != null) {
            final StorageReference arquivoReference = storageReference.child(System.currentTimeMillis() + "." + getExtensaoArquivo(uriImagem));

            uploadTask = arquivoReference.putFile(uriImagem).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                private static final String TAG = "uploadDoArquivo";

                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pbUpload.setProgress(0);
                        }
                    }, 500);
                    Toast.makeText(getApplicationContext(), "Upload completo!", Toast.LENGTH_SHORT).show();

                    Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful()) ;
                    Uri downloadUri = uriTask.getResult();

                    Log.d(TAG, ": Firebase URL: " + downloadUri.toString());
                    // Upload upload = new Upload(edtNomeArquivo.getText().toString().trim(), downloadUri.toString());
                    //
                    String uploadId = collectionReference.document().getId();

                    Map<String, Object> map = new HashMap<>();
                    map.put("url", downloadUri.toString());
                    //

                    collectionReference.document().set(map);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progresso = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    pbUpload.setProgress((int) progresso);
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Nenhum arquivo selecionado!", Toast.LENGTH_SHORT).show();
        }
    }

    private String getExtensaoArquivo(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
 */