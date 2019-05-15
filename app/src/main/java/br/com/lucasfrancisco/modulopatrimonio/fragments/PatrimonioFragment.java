package br.com.lucasfrancisco.modulopatrimonio.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import br.com.lucasfrancisco.modulopatrimonio.R;
import br.com.lucasfrancisco.modulopatrimonio.activities.MainActivity;
import br.com.lucasfrancisco.modulopatrimonio.adapters.PatrimonioAdapter;
import br.com.lucasfrancisco.modulopatrimonio.dao.preferences.SharedPreferencesEmpresa;
import br.com.lucasfrancisco.modulopatrimonio.fragments.edits.EditPatrimonioFragment;
import br.com.lucasfrancisco.modulopatrimonio.fragments.news.NovoPatrimonioFragment;
import br.com.lucasfrancisco.modulopatrimonio.interfaces.CommunicatePesquisaFragment;
import br.com.lucasfrancisco.modulopatrimonio.interfaces.RCYDocumentSnapshotClickListener;
import br.com.lucasfrancisco.modulopatrimonio.models.Endereco;
import br.com.lucasfrancisco.modulopatrimonio.models.Patrimonio;
import br.com.lucasfrancisco.modulopatrimonio.styles.StylesXLSX;

import static android.app.Activity.RESULT_OK;

public class PatrimonioFragment extends Fragment {
    private static final int REQUEST_CREATE_XLSX = 10;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = firebaseFirestore.collection("Empresas");
    private Query query;

    private CommunicatePesquisaFragment communicatePesquisaFragment;

    private PatrimonioAdapter patrimonioAdapter;
    private FirestoreRecyclerOptions<Patrimonio> firestoreRecyclerOptions;

    private RecyclerView rcyPatrimonios;
    private FloatingActionButton fabNovo, fabXls, fabFiltro;

    private ArrayAdapter adapter;
    private String empresaEscolhida = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patrimonio, container, false);
        communicatePesquisaFragment.onSetFilter(setListFiltros());

        query = collectionReference.document("4081 - SENAI Indaial").collection("Patrimonios").orderBy("plaqueta", Query.Direction.ASCENDING);
        firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Patrimonio>().setQuery(query, Patrimonio.class).build();
        patrimonioAdapter = new PatrimonioAdapter(firestoreRecyclerOptions);

        rcyPatrimonios = (RecyclerView) view.findViewById(R.id.rcyPatrimonios);
        rcyPatrimonios.setHasFixedSize(true);
        rcyPatrimonios.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcyPatrimonios.setAdapter(patrimonioAdapter);

        fabNovo = (FloatingActionButton) view.findViewById(R.id.fabNovo);
        fabXls = (FloatingActionButton) view.findViewById(R.id.fabXls);
        fabFiltro = (FloatingActionButton) view.findViewById(R.id.fabFiltro);

        // Métodos de eventos
        getFabFiltro();
        getFabXls();
        getFabNovo();
        getAdapterItemTouch();
        getRecyclerViewClickListener();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CREATE_XLSX: // Abre a galeria
                    requestCreateXLSX(data);
                    break;
            }
        }
    }

    public void getFabXls() {
        fabXls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.putExtra(Intent.EXTRA_TITLE, "Patrimônios.xlsx");
                intent.setType("*/*");
                startActivityForResult(intent, REQUEST_CREATE_XLSX);
            }
        });
    }

    public void getFabNovo() {
        fabNovo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fmlPesquisa, new OpcoesMenuFragment()).commit();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fmlConteudo, new NovoPatrimonioFragment()).commit();
                MainActivity.fragment = new NovoPatrimonioFragment();
            }
        });
    }

    public void getFabFiltro() {
        fabFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View viewDialog = getLayoutInflater().inflate(R.layout.dialog_filtro_patrimonio, null);
                Spinner spnEmpresa = (Spinner) viewDialog.findViewById(R.id.spnEmpresa);
                Spinner spnSetor = (Spinner) viewDialog.findViewById(R.id.spnSetor);
                Button btnFiltrar = (Button) viewDialog.findViewById(R.id.btnFiltrar);

                spinnerEmpresas(spnEmpresa, spnSetor);
                btnFiltrar(btnFiltrar);

                builder.setView(viewDialog);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    public void spinnerEmpresas(final Spinner spnEmpresa, final Spinner spnSetor) {
        final ArrayList<String> list = new ArrayList<>();
        CollectionReference collectionReference = firebaseFirestore.collection("Empresas");

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    list.add(documentSnapshot.getId());
                }

                adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, list);
                spnEmpresa.setAdapter(adapter);

                spnEmpresa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        empresaEscolhida = spnEmpresa.getSelectedItem().toString();
                        spinnerSetores(spnSetor);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        });
    }

    public void spinnerSetores(final Spinner spnSetor) {
        final ArrayList<String> list = new ArrayList<>();
        CollectionReference collectionReference = firebaseFirestore.collection("Empresas").document(empresaEscolhida).collection("Setores");

        collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    list.add(documentSnapshot.getId());
                }

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

    public void btnFiltrar(Button btnFiltrar) {
        btnFiltrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query = collectionReference.document(empresaEscolhida).collection("Patrimonios").orderBy("plaqueta", Query.Direction.ASCENDING);
                firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Patrimonio>().setQuery(query, Patrimonio.class).build();
                patrimonioAdapter = new PatrimonioAdapter(firestoreRecyclerOptions);

                rcyPatrimonios.setHasFixedSize(true);
                rcyPatrimonios.setLayoutManager(new LinearLayoutManager(getActivity()));
                rcyPatrimonios.setAdapter(patrimonioAdapter);

                patrimonioAdapter.startListening();

                getAdapterItemTouch();
                getRecyclerViewClickListener();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            communicatePesquisaFragment = (CommunicatePesquisaFragment) context;
        } catch (Exception e) {
            Log.w("PatrimonioFragment", e.toString());
            Toast.makeText(getActivity(), "Erro: " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        patrimonioAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        patrimonioAdapter.stopListening();
    }


    public void getRecyclerViewClickListener() {
        patrimonioAdapter.setRCYDocumentSnapshotClickListener(new RCYDocumentSnapshotClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int posicao) {
                Patrimonio patrimonio = documentSnapshot.toObject(Patrimonio.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("patrimonio", patrimonio);
                bundle.putString("empresa", patrimonio.getSetor().getEmpresa().getCodigo() + " - " + patrimonio.getSetor().getEmpresa().getFantasia() + " " + patrimonio.getSetor().getEmpresa().getEndereco().getCidade());
                bundle.putString("setor", patrimonio.getSetor().getBloco() + " - " + patrimonio.getSetor().getSala());
                bundle.putString("objeto", patrimonio.getObjeto().getTipo() + " - " + patrimonio.getObjeto().getMarca() + " " + patrimonio.getObjeto().getModelo());

                Fragment fragment = new EditPatrimonioFragment();
                fragment.setArguments(bundle);

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fmlPesquisa, new OpcoesMenuFragment()).commit();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fmlConteudo, fragment).commit();
                MainActivity.fragment = fragment;
            }

            @Override
            public boolean onItemLongClick(DocumentSnapshot documentSnapshot, int posicao) {
                Patrimonio patrimonio = documentSnapshot.toObject(Patrimonio.class);
                Toast.makeText(getActivity(), "Marca: " + patrimonio.getObjeto().getMarca(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    public void getAdapterItemTouch() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                patrimonioAdapter.excluir(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(rcyPatrimonios);
    }

    // Dados entre Fragments
    public void pesquisar(String pesquisa, String empresa, long limite) {
        ArrayList<String> listFiltros = setListFiltros(null);

        for (int i = 0; i < listFiltros.size(); i++) {
            if (listFiltros.get(i).contains(empresa)) {
                empresa = listFiltros.get(i); // Recupera o id do documento através de listFiltros
            }
        }

        // Coloca a primeira letra da pesquisa em maiúsculo
        if (!pesquisa.equals("")) {
            pesquisa = pesquisa.substring(0, 1).toUpperCase().concat(pesquisa.substring(1));
        }

        query = collectionReference.document(empresa).collection("Patrimonios").orderBy("plaqueta").startAt(pesquisa).endAt(pesquisa + "\uf8ff").limit(limite);
        FirestoreRecyclerOptions<Patrimonio> firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Patrimonio>().setQuery(query, Patrimonio.class).build();
        patrimonioAdapter = new PatrimonioAdapter(firestoreRecyclerOptions);

        rcyPatrimonios.setHasFixedSize(true);
        rcyPatrimonios.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcyPatrimonios.setAdapter(patrimonioAdapter);

        patrimonioAdapter.startListening();

        getAdapterItemTouch();
        getRecyclerViewClickListener();
    }

    // Sobrecarga
    public ArrayList<String> setListFiltros(ArrayList<String> list) {
        SharedPreferencesEmpresa sharedPreferencesEmpresa = new SharedPreferencesEmpresa();
        ArrayList<String> listFiltros = sharedPreferencesEmpresa.buscar(getContext());
        return listFiltros;
    }

    // Sobrecarga
    public ArrayList<String> setListFiltros() {
        SharedPreferencesEmpresa sharedPreferencesEmpresa = new SharedPreferencesEmpresa();
        ArrayList<String> listAux = sharedPreferencesEmpresa.buscar(getContext());
        ArrayList<String> listFiltros = new ArrayList<>();

        for (int i = 0; i < listAux.size(); i++) {
            String codigo = listAux.get(i).substring(0, 4); // Diminui as String do ArrayList para melhorar o visual do AppBar com o spnFiltro
            listFiltros.add(codigo);
        }
        return listFiltros;
    }

    // Cria planilha
    private void requestCreateXLSX(final Intent intent) {
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                OutputStream outputStream = null;
                try {
                    outputStream = getActivity().getContentResolver().openOutputStream(intent.getData());
                } catch (FileNotFoundException e) {
                    Log.d("PatrimonioFragment", e.getMessage());
                }

                List<Patrimonio> patrimonios = new ArrayList<>();
                XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
                XSSFSheet xssfSheet = xssfWorkbook.createSheet("Patrimônios");
                String titulo = "Patrimônios " + empresaEscolhida;
                String[] subtitulos = {"Plaqueta", "Tipo", "Marca", "Modelo", "Empresa", "Bloco", "Sala"};

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Patrimonio patrimonio = documentSnapshot.toObject(Patrimonio.class);
                    patrimonios.add(patrimonio);
                }

                //desliga e liga linhas de grade
                xssfSheet.setDisplayGridlines(false); // Grades
                xssfSheet.setPrintGridlines(false);
                xssfSheet.setFitToPage(true);
                xssfSheet.setHorizontallyCenter(true);
                PrintSetup printSetup = xssfSheet.getPrintSetup();
                printSetup.setLandscape(true);

                // Cria título
                Row rowTitulo = xssfSheet.createRow(0);
                for (int i = 1; i < subtitulos.length; i++) {
                    rowTitulo.createCell(i).setCellStyle(StylesXLSX.createStylesTitle(xssfWorkbook));
                }
                Cell cellTitulo = rowTitulo.createCell(0);
                cellTitulo.setCellValue(titulo);
                cellTitulo.setCellStyle(StylesXLSX.createStylesTitle(xssfWorkbook));
                xssfSheet.addMergedRegion(CellRangeAddress.valueOf("$A$1:$G$1"));

                // Cria subtítulo
                Row rowSubtitulo = xssfSheet.createRow(1);
                for (int i = 0; i < subtitulos.length; i++) {
                    xssfSheet.setColumnWidth(i, 15 * 256); // setColumnWidth(indice, largura)
                    Cell cell = rowSubtitulo.createCell(i);
                    cell.setCellValue(subtitulos[i]);
                    cell.setCellStyle(StylesXLSX.createStylesSubtitle(xssfWorkbook));
                }

                int rownum = 2;
                for (Patrimonio patrimonio : patrimonios) {
                    CellStyle styleCorpo;
                    if (rownum % 2 == 0) {
                        styleCorpo = StylesXLSX.createStylesBody1(xssfWorkbook);
                    } else {
                        styleCorpo = StylesXLSX.createStylesBody2(xssfWorkbook);
                    }

                    Row row = xssfSheet.createRow(rownum++);
                    int cellnum = 0;
                    Cell cellPalqueta = row.createCell(cellnum++);
                    cellPalqueta.setCellValue(patrimonio.getPlaqueta());
                    cellPalqueta.setCellStyle(styleCorpo);

                    Cell cellTipo = row.createCell(cellnum++);
                    cellTipo.setCellValue(patrimonio.getObjeto().getTipo());
                    cellTipo.setCellStyle(styleCorpo);

                    Cell cellMarca = row.createCell(cellnum++);
                    cellMarca.setCellValue(patrimonio.getObjeto().getMarca());
                    cellMarca.setCellStyle(styleCorpo);

                    Cell cellModelo = row.createCell(cellnum++);
                    cellModelo.setCellValue(patrimonio.getObjeto().getModelo());
                    cellModelo.setCellStyle(styleCorpo);

                    Cell cellEmpresa = row.createCell(cellnum++);
                    cellEmpresa.setCellValue(patrimonio.getSetor().getEmpresa().getFantasia());
                    cellEmpresa.setCellStyle(styleCorpo);

                    Cell cellBloco = row.createCell(cellnum++);
                    cellBloco.setCellValue(patrimonio.getSetor().getBloco());
                    cellBloco.setCellStyle(styleCorpo);

                    Cell cellSala = row.createCell(cellnum++);
                    cellSala.setCellValue(patrimonio.getSetor().getSala());
                    cellSala.setCellStyle(styleCorpo);
                }

                try {
                    xssfWorkbook.write(outputStream);
                    outputStream.close();
                    Log.d("PatrimonioFragment", "Arquivo Excel criado com sucesso!");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.d("PatrimonioFragment", "Arquivo não encontrado!");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("PatrimonioFragment", "Erro na edição do arquivo!");
                }
            }
        });
    }
}


