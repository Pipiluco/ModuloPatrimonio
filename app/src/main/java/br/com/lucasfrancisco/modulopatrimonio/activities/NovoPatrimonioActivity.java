package br.com.lucasfrancisco.modulopatrimonio.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import br.com.lucasfrancisco.modulopatrimonio.R;
import br.com.lucasfrancisco.modulopatrimonio.adapters.ImagemAdapter;
import br.com.lucasfrancisco.modulopatrimonio.interfaces.RCYViewClickListener;
import br.com.lucasfrancisco.modulopatrimonio.models.Imagem;
import br.com.lucasfrancisco.modulopatrimonio.models.Patrimonio;
import br.com.lucasfrancisco.modulopatrimonio.models.Setor;

public class NovoPatrimonioActivity extends AppCompatActivity {
    private static final int REQUEST_LOAD_IMAGE = 1;
    private static final int REQUEST_SCAN_CODE = 101;
    private static final int REQUEST_CAMERA_CODE = 3;

    private Spinner spnEmpresa, spnSetor;
    private EditText edtPlaqueta, edtTipo, edtMarca, edtModelo;
    private ImageButton imbScanner;
    private RecyclerView rcyImagens;
    private FloatingActionButton fabNovaFoto, fabGaleria;
    private Toolbar tbrBottomMain;
    private ProgressDialog progressDialog;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    private ArrayAdapter adapter;
    private ImagemAdapter imagemAdapter;
    private ArrayList<String> listEmpresas;
    private ArrayList<String> listPatrimonios;
    private List<Imagem> imagens;

    private Activity activity = this;
    private Uri uriImagemCamera;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_patrimonio);

        getListPatrimonios();

        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle(getString(R.string.novo_patrimonio));

        spnEmpresa = (Spinner) findViewById(R.id.spnEmpresa);
        spnSetor = (Spinner) findViewById(R.id.spnSetor);
        edtPlaqueta = (EditText) findViewById(R.id.edtPlaqueta);
        edtTipo = (EditText) findViewById(R.id.edtTipo);
        edtMarca = (EditText) findViewById(R.id.edtMarca);
        edtModelo = (EditText) findViewById(R.id.edtModelo);
        imbScanner = (ImageButton) findViewById(R.id.imbScanner);
        rcyImagens = (RecyclerView) findViewById(R.id.rcyImagens);
        fabNovaFoto = (FloatingActionButton) findViewById(R.id.fabNovaFoto);
        fabGaleria = (FloatingActionButton) findViewById(R.id.fabGaleria);
        tbrBottomMain = (Toolbar) findViewById(R.id.incTbrBottom);

        progressDialog = new ProgressDialog(this);
        imagens = new ArrayList<>();
        imagemAdapter = new ImagemAdapter(imagens, getApplicationContext());

        rcyImagens.setLayoutManager(new LinearLayoutManager(this));
        rcyImagens.setHasFixedSize(true);
        rcyImagens.setItemViewCacheSize(20);
        rcyImagens.setAdapter(imagemAdapter);
        imagemAdapter.notifyDataSetChanged();

        getPermissoes();
        getSpinnerEmpresas();
        getFabNovaFoto();
        getFabGaleria();
        getTbrBottomMain();
        getItemTouch();
        getClickRecyclerView();
        getImbScanner();
    }

    @Override
    protected void onStart() {
        super.onStart();
        imagemAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_LOAD_IMAGE: // Abre a galeria
                    requestLoadImage(data);
                    break;
                case REQUEST_SCAN_CODE: // Abre o scanner
                    requestScanCode(resultCode, data);
                    break;
                case REQUEST_CAMERA_CODE: // Abre a câmera
                    requestCameraCode();
                    break;
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

    // Salva patrimônio
    public void salvar() { // Estável OK
        storageReference = FirebaseStorage.getInstance().getReference();
        getListPatrimonios();

        if (spnEmpresa.getSelectedItem() == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.necessario_empresa), Toast.LENGTH_SHORT).show();
            return;
        }

        if (spnSetor.getSelectedItem() == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.necessario_setor), Toast.LENGTH_SHORT).show();
            return;
        }

        final Setor setor = (Setor) spnSetor.getSelectedItem();
        final String nomeEmpresa = spnEmpresa.getSelectedItem().toString();
        final String plaqueta = edtPlaqueta.getText().toString();
        final String tipo = edtTipo.getText().toString();
        final String marca = edtMarca.getText().toString();
        final String modelo = edtModelo.getText().toString();
        Boolean isPatrimonio = false;
        final CollectionReference collectionReference = firebaseFirestore.collection("Empresas");

        // Verifica se o patrimônio já existe no banco
        for (int i = 0; i < listPatrimonios.size(); i++) {
            if (plaqueta.equals(listPatrimonios.get(i))) {
                isPatrimonio = true;
                Toast.makeText(getApplicationContext(), getString(R.string.patrimonio_ja_existe) + " (" + plaqueta + ")", Toast.LENGTH_SHORT).show();
            }
        }

        if (!isPatrimonio) {
            if (plaqueta.trim().isEmpty() || tipo.trim().isEmpty() || marca.trim().isEmpty() || modelo.trim().isEmpty()) {
                Toast.makeText(getApplicationContext(), getString(R.string.dados_incompletos), Toast.LENGTH_SHORT).show();
            } else {
                //
                progressDialog.setTitle(getString(R.string.salvando) + " " + plaqueta);
                progressDialog.setMessage(getString(R.string.por_favor_aguarde));
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                if (imagens.size() > 0) { // Salva patrimônio com imagem
                    for (int i = 0; i < imagens.size(); i++) {
                        final String nomeArquivo = plaqueta + "_" + System.currentTimeMillis() + "." + getExtensaoArquivo(Uri.parse(imagens.get(i).getUrlLocal()));
                        StorageReference uploadReference = storageReference.child("Imagens/Patrimonios/" + plaqueta).child(nomeArquivo);
                        final int finalI = i;

                        uploadReference.putFile(Uri.parse(imagens.get(finalI).getUrlLocal())).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                progressDialog.setMessage("Salvando " + finalI + " de " + imagens.size());
                                imagens.get(finalI).setEnviada(true);
                                imagens.get(finalI).setNome(nomeArquivo);
                                //
                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!uriTask.isSuccessful()) ;
                                imagens.get(finalI).setUrlRemota(uriTask.getResult().toString());
                                imagemAdapter.notifyDataSetChanged();

                                if (finalI == imagens.size() - 1) {
                                    Patrimonio patrimonio = new Patrimonio(tipo, marca, modelo, plaqueta, true, setor, imagens); // imagens
                                    collectionReference.document(nomeEmpresa).collection("Patrimonios").document(plaqueta).set(patrimonio);
                                    Toast.makeText(getApplicationContext(), getString(R.string.patrimonio_salvo), Toast.LENGTH_SHORT).show();
                                    cleanForm();
                                    progressDialog.cancel();
                                    getListPatrimonios();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.cancel();
                                Toast.makeText(getApplicationContext(), "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            }
                        });
                    }
                } else { // Salva patrimônio sem imagem
                    Patrimonio patrimonio = new Patrimonio(tipo, marca, modelo, plaqueta, true, setor, imagens); // imagens
                    collectionReference.document(nomeEmpresa).collection("Patrimonios").document(plaqueta).set(patrimonio);
                    Toast.makeText(getApplicationContext(), getString(R.string.patrimonio_salvo), Toast.LENGTH_SHORT).show();
                    cleanForm();
                    progressDialog.cancel();
                    getListPatrimonios();
                }
            }
        }
    }

    // Salva patrimônio
    public void salvar2() { // Estável OK
        storageReference = FirebaseStorage.getInstance().getReference();
        getListPatrimonios();

        if (spnEmpresa.getSelectedItem() == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.necessario_empresa), Toast.LENGTH_SHORT).show();
            return;
        }

        if (spnSetor.getSelectedItem() == null) {
            Toast.makeText(getApplicationContext(), getString(R.string.necessario_setor), Toast.LENGTH_SHORT).show();
            return;
        }

        final Setor setor = (Setor) spnSetor.getSelectedItem();
        final String nomeEmpresa = spnEmpresa.getSelectedItem().toString();
        final String plaqueta = edtPlaqueta.getText().toString();
        final String tipo = edtTipo.getText().toString();
        final String marca = edtMarca.getText().toString();
        final String modelo = edtModelo.getText().toString();
        Boolean isPatrimonio = false;
        final CollectionReference collectionReference = firebaseFirestore.collection("Empresas");

        // Verifica se o patrimônio já existe no banco
        for (int i = 0; i < listPatrimonios.size(); i++) {
            if (plaqueta.equals(listPatrimonios.get(i))) {
                isPatrimonio = true;
                Toast.makeText(getApplicationContext(), getString(R.string.patrimonio_ja_existe) + " (" + plaqueta + ")", Toast.LENGTH_SHORT).show();
            }
        }

        if (!isPatrimonio) {
            if (plaqueta.trim().isEmpty() || tipo.trim().isEmpty() || marca.trim().isEmpty() || modelo.trim().isEmpty()) {
                Toast.makeText(getApplicationContext(), getString(R.string.dados_incompletos), Toast.LENGTH_SHORT).show();
            } else {
                //
                progressDialog.setTitle(getString(R.string.salvando) + " " + plaqueta);
                progressDialog.setMessage(getString(R.string.por_favor_aguarde));
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                if (imagens.size() > 0) { // Salva patrimônio com imagem
                    for (int i = 0; i < imagens.size(); i++) {
                        final String nomeArquivo = plaqueta + "_" + System.currentTimeMillis() + "." + getExtensaoArquivo(Uri.parse(imagens.get(i).getUrlLocal()));
                        StorageReference uploadReference = storageReference.child("Imagens/Patrimonios/" + plaqueta).child(nomeArquivo);
                        final int finalI = i;

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
                                    Patrimonio patrimonio = new Patrimonio(tipo, marca, modelo, plaqueta, true, setor, imagens); // imagens
                                    collectionReference.document(nomeEmpresa).collection("Patrimonios").document(plaqueta).set(patrimonio);
                                    Toast.makeText(getApplicationContext(), getString(R.string.patrimonio_salvo), Toast.LENGTH_SHORT).show();
                                    cleanForm();
                                    progressDialog.cancel();
                                    getListPatrimonios();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.cancel();
                                Toast.makeText(getApplicationContext(), "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } else { // Salva patrimônio sem imagem
                    Patrimonio patrimonio = new Patrimonio(tipo, marca, modelo, plaqueta, true, setor, imagens); // imagens
                    collectionReference.document(nomeEmpresa).collection("Patrimonios").document(plaqueta).set(patrimonio);
                    Toast.makeText(getApplicationContext(), getString(R.string.patrimonio_salvo), Toast.LENGTH_SHORT).show();
                    cleanForm();
                    progressDialog.cancel();
                    getListPatrimonios();
                }
            }
        }
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

    private void requestScanCode(int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(IntentIntegrator.REQUEST_CODE, resultCode, data);
        String resultado = result.getContents();
        edtPlaqueta.setText(removePrimeiroZero(resultado));
    }

    private void requestCameraCode() {
        String nomeArquivo = getNomeArquivo(uriImagemCamera);
        Imagem imagem = new Imagem(nomeArquivo, uriImagemCamera.toString(), null, false);
        imagens.add(imagem);
        imagemAdapter.notifyDataSetChanged();
    }

    private String getExtensaoArquivo(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
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

    public void getImbScanner() {
        imbScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt(getString(R.string.aponte_a_camera_para_codigo));
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);

                Intent intent = integrator.createScanIntent();
                startActivityForResult(intent, REQUEST_SCAN_CODE);
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

    // Click em item RecyclerView
    public void getClickRecyclerView() {
        imagemAdapter.setRcyViewClickListener(new RCYViewClickListener() {
            @Override
            public void onItemClick(View view, int posicao) {
                Toast.makeText(getApplicationContext(), "Curto: " + posicao, Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean onItemLongClick(View view, int posicao) {
                Toast.makeText(getApplicationContext(), "Longo: " + posicao, Toast.LENGTH_SHORT).show();
                return true;
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

    // Limpa campos de texto e lista
    public void cleanForm() {
        edtPlaqueta.setText("");
        edtTipo.setText("");
        edtMarca.setText("");
        edtModelo.setText("");
        imagens.clear();
        imagemAdapter.notifyDataSetChanged();
    }

    public String removePrimeiroZero(String plaqueta) {
        if (!plaqueta.isEmpty()) {
            if (plaqueta.substring(0, 1).equals("0")) {
                plaqueta = plaqueta.substring(1);
            }
        }
        return plaqueta;
    }

    public void getPermissoes() {
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        }
    }
}
