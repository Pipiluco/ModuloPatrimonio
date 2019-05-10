package br.com.lucasfrancisco.modulopatrimonio.fragments.news;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.lucasfrancisco.modulopatrimonio.R;
import br.com.lucasfrancisco.modulopatrimonio.activities.news.NovoSetorActivity;
import br.com.lucasfrancisco.modulopatrimonio.adapters.ImagemAdapter;
import br.com.lucasfrancisco.modulopatrimonio.interfaces.CommunicateOpcoesMenuFragment;
import br.com.lucasfrancisco.modulopatrimonio.interfaces.RCYViewClickListener;
import br.com.lucasfrancisco.modulopatrimonio.models.Imagem;
import br.com.lucasfrancisco.modulopatrimonio.models.Objeto;
import br.com.lucasfrancisco.modulopatrimonio.models.Patrimonio;
import br.com.lucasfrancisco.modulopatrimonio.models.Setor;
import br.com.lucasfrancisco.modulopatrimonio.models.Usuario;

import static android.app.Activity.RESULT_OK;

public class NovoPatrimonioFragment extends Fragment {
    private static final int REQUEST_LOAD_IMAGE = 1;
    private static final int REQUEST_SCAN_CODE = 101;
    private static final int REQUEST_CAMERA_CODE = 3;

    private CommunicateOpcoesMenuFragment communicateOpcoesMenuFragment;

    private Spinner spnEmpresa, spnSetor, spnObjeto;
    private EditText edtPlaqueta;
    private ImageButton imbScanner;
    private RecyclerView rcyImagens;
    private FloatingActionButton fabNovaFoto, fabGaleria, fabNovoSetor;
    private ProgressDialog progressDialog;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private ArrayAdapter adapter;
    private ImagemAdapter imagemAdapter;
    private ArrayList<String> listEmpresas;
    private ArrayList<String> listPatrimonios;
    private List<Imagem> imagens;
    private int contador = 0;

    private Activity activity = getActivity();
    private Uri uriImagemCamera;

    private NotificationManagerCompat notificationManagerCompat;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_novo_patrimonio, container, false);
        communicateOpcoesMenuFragment.onSetFragment(setFragment());

        getListPatrimonios();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        spnEmpresa = (Spinner) view.findViewById(R.id.spnEmpresa);
        spnSetor = (Spinner) view.findViewById(R.id.spnSetor);
        spnObjeto = (Spinner) view.findViewById(R.id.spnObjeto);
        edtPlaqueta = (EditText) view.findViewById(R.id.edtPlaqueta);
        imbScanner = (ImageButton) view.findViewById(R.id.imbScanner);
        rcyImagens = (RecyclerView) view.findViewById(R.id.rcyImagens);
        fabNovaFoto = (FloatingActionButton) view.findViewById(R.id.fabNovaFoto);
        fabGaleria = (FloatingActionButton) view.findViewById(R.id.fabGaleria);
        fabNovoSetor = (FloatingActionButton) view.findViewById(R.id.fabNovoSetor);

        progressDialog = new ProgressDialog(getActivity());
        imagens = new ArrayList<>();
        imagemAdapter = new ImagemAdapter(imagens, getActivity());

        notificationManagerCompat = NotificationManagerCompat.from(getActivity());

        rcyImagens.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcyImagens.setHasFixedSize(true);
        rcyImagens.setItemViewCacheSize(20);
        rcyImagens.setAdapter(imagemAdapter);
        imagemAdapter.notifyDataSetChanged();

        getPermissoes();
        getSpinnerEmpresas();
        getSpinnerObjetos();
        getFabNovaFoto();
        getFabGaleria();
        getFabNovoSetor();
        getItemTouch();
        getClickRecyclerView();
        getImbScanner();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        imagemAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            communicateOpcoesMenuFragment = (CommunicateOpcoesMenuFragment) context;
        } catch (Exception e) {
            Log.w("NovoPatrimonioFragment", e.toString());
            Toast.makeText(getActivity(), "Erro: " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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


    // Salva patrimônio ///////////////////////////////////////////////////////////////////////////
    public void salvar() { // Estável OK
        getListPatrimonios();

        if (spnEmpresa.getSelectedItem() == null) {
            Toast.makeText(getActivity(), getString(R.string.necessario_empresa), Toast.LENGTH_SHORT).show();
            return;
        }

        if (spnSetor.getSelectedItem() == null) {
            Toast.makeText(getActivity(), getString(R.string.necessario_setor), Toast.LENGTH_SHORT).show();
            return;
        }

        final Setor setor = (Setor) spnSetor.getSelectedItem();
        final Objeto objeto = (Objeto) spnObjeto.getSelectedItem();
        final Usuario criador = new Usuario(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getEmail(), firebaseUser.getPhotoUrl().toString(), null, null);
        final String nomeEmpresa = spnEmpresa.getSelectedItem().toString();
        final String plaqueta = edtPlaqueta.getText().toString();
        final Date dataCriacao = Timestamp.now().toDate();
        boolean isPatrimonio = false;
        final CollectionReference collectionReference = firebaseFirestore.collection("Empresas");

        // Verifica se o patrimônio já existe no banco
        for (int i = 0; i < listPatrimonios.size(); i++) {
            if (plaqueta.equals(listPatrimonios.get(i))) {
                isPatrimonio = true;
                Toast.makeText(getActivity(), getString(R.string.patrimonio_ja_existe) + " (" + plaqueta + ")", Toast.LENGTH_SHORT).show();
            }
        }

        // Se o patrimônio não existe ele pode ser criado
        if (!isPatrimonio) {
            if (plaqueta.trim().isEmpty()) {
                Toast.makeText(getActivity(), getString(R.string.dados_incompletos), Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.setTitle(getString(R.string.salvando) + " " + plaqueta);
                progressDialog.setMessage(getString(R.string.por_favor_aguarde));
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                if (imagens.size() > 0) { // Salva patrimônio com imagem
                    for (int i = 0; i < imagens.size(); i++) {
                        final String nome = plaqueta + "_" + System.currentTimeMillis() + "." + getExtensaoArquivo(Uri.parse(imagens.get(i).getUrlLocal()));
                        final StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Imagens/Patrimonios/" + plaqueta).child(nome);

                        storageReference.putFile(Uri.parse(imagens.get(i).getUrlLocal())).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() { // Envia o arquivo
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() { // Recupera a url do arquivo já envido
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        imagens.get(contador).setEnviada(true);
                                        imagens.get(contador).setNome(nome);
                                        imagens.get(contador).setUrlRemota(String.valueOf(uri));
                                        imagemAdapter.notifyDataSetChanged();

                                        contador = contador + 1;

                                        if (imagens.size() == contador) {
                                            Patrimonio patrimonio = new Patrimonio(criador, null, dataCriacao, null, plaqueta, true, setor, objeto, imagens);

                                            collectionReference.document(nomeEmpresa).collection("Patrimonios").document(plaqueta).set(patrimonio).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    progressDialog.cancel();
                                                    cleanForm();
                                                    Toast.makeText(getActivity(), getString(R.string.patrimonio_salvo), Toast.LENGTH_SHORT).show();
                                                    getListPatrimonios();
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        });
                    }
                } else { // Salva patrimônio sem imagem
                    Patrimonio patrimonio = new Patrimonio(criador, null, dataCriacao, null, plaqueta, true, setor, objeto, imagens); // imagens
                    collectionReference.document(nomeEmpresa).collection("Patrimonios").document(plaqueta).set(patrimonio);
                    Toast.makeText(getActivity(), getString(R.string.patrimonio_salvo), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), getString(R.string.imagem_ja_esta_na_lista), Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getActivity(), getString(R.string.imagem_ja_esta_na_lista), Toast.LENGTH_LONG).show();
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
        ContentResolver contentResolver = getActivity().getContentResolver();
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
                adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, listEmpresas);
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
                adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, list);
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

    private void getSpinnerObjetos() {
        final List<Objeto> list = new ArrayList<>();
        CollectionReference collectionReference = firebaseFirestore.collection("Objetos");

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Objeto objeto = documentSnapshot.toObject(Objeto.class);
                    list.add(objeto);
                }
                adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, list);
                spnObjeto.setAdapter(adapter);
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

                uriImagemCamera = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

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

    public void getFabNovoSetor() {
        fabNovoSetor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), NovoSetorActivity.class));
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
                Toast.makeText(getActivity(), "Curto: " + posicao, Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean onItemLongClick(View view, int posicao) {
                Toast.makeText(getActivity(), "Longo: " + posicao, Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    // Retorna o nome de um arquivo selecionado no gerenciador de arquivos
    public String getNomeArquivo(Uri uri) {
        String resultado = null;

        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
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
        contador = 0;
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

    public void setMenuItem(String opcao) {
        Toast.makeText(getActivity(), opcao, Toast.LENGTH_LONG).show();

        switch (opcao) {
            case "Salvar":
                salvar();
                break;
        }
    }

    public String setFragment() {
        return "NovoPatrimonioFragment";
    }
}
