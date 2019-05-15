package br.com.lucasfrancisco.modulopatrimonio.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
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
import br.com.lucasfrancisco.modulopatrimonio.adapters.EnderecoAdapter;
import br.com.lucasfrancisco.modulopatrimonio.fragments.edits.EditEnderecoFragment;
import br.com.lucasfrancisco.modulopatrimonio.fragments.news.NovoEnderecoFragment;
import br.com.lucasfrancisco.modulopatrimonio.interfaces.CommunicatePesquisaFragment;
import br.com.lucasfrancisco.modulopatrimonio.interfaces.RCYDocumentSnapshotClickListener;
import br.com.lucasfrancisco.modulopatrimonio.models.Endereco;
import br.com.lucasfrancisco.modulopatrimonio.styles.StylesXLSX;

import static android.app.Activity.RESULT_OK;

public class EnderecoFragment extends Fragment {
    private static final int REQUEST_CREATE_XLSX = 10;

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = firebaseFirestore.collection("Enderecos");
    private Query query;
    private FirestoreRecyclerOptions<Endereco> firestoreRecyclerOptions;

    private CommunicatePesquisaFragment communicatePesquisaFragment;

    private EnderecoAdapter enderecoAdapter;

    private FloatingActionMenu famEndereco;
    private FloatingActionButton fabNovo, fabXls;
    private RecyclerView rcyEnderecos;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_endereco, container, false);
        communicatePesquisaFragment.onSetFilter(setListFiltros());

        rcyEnderecos = (RecyclerView) view.findViewById(R.id.rcyEnderecos);
        famEndereco = (FloatingActionMenu) view.findViewById(R.id.famEndereco);
        fabNovo = (FloatingActionButton) view.findViewById(R.id.fabNovo);
        fabXls = (FloatingActionButton) view.findViewById(R.id.fabXls);

        // Carrrego inicial do rcyEnderecos
        query = collectionReference.orderBy("cep").limit(100);
        firestoreRecyclerOptions = new FirestoreRecyclerOptions.Builder<Endereco>().setQuery(query, Endereco.class).build();
        enderecoAdapter = new EnderecoAdapter(firestoreRecyclerOptions);

        rcyEnderecos.setHasFixedSize(true);
        rcyEnderecos.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcyEnderecos.setAdapter(enderecoAdapter);

        // Métodos para eventos
        getFabXls();
        getFabNovo();
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            communicatePesquisaFragment = (CommunicatePesquisaFragment) context;
        } catch (Exception e) {
            Log.w("EnderecoFragment", e.toString());
            Toast.makeText(getActivity(), "Erro: " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        enderecoAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        enderecoAdapter.stopListening();
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
                    Log.d("EnderecoFragment", e.getMessage());
                }

                List<Endereco> enderecos = new ArrayList<>();
                XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
                XSSFSheet xssfSheet = xssfWorkbook.createSheet("Endereços");
                String titulo = "Endereços SENAI";
                String[] subtitulos = {"Rua", "Número", "CEP", "Bairro", "Cidade", "Estado", "Pais"};

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Endereco endereco = documentSnapshot.toObject(Endereco.class);
                    enderecos.add(endereco);
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
                for (Endereco endereco : enderecos) {
                    CellStyle styleCorpo;
                    if (rownum % 2 == 0) {
                        styleCorpo = StylesXLSX.createStylesBody1(xssfWorkbook);
                    } else {
                        styleCorpo = StylesXLSX.createStylesBody2(xssfWorkbook);
                    }

                    Row row = xssfSheet.createRow(rownum++);
                    int cellnum = 0;
                    Cell cellRua = row.createCell(cellnum++);
                    cellRua.setCellValue(endereco.getRua());
                    cellRua.setCellStyle(styleCorpo);

                    Cell cellNumero = row.createCell(cellnum++);
                    cellNumero.setCellValue(endereco.getNumero());
                    cellNumero.setCellStyle(styleCorpo);

                    Cell cellCEP = row.createCell(cellnum++);
                    cellCEP.setCellValue(endereco.getCEP());
                    cellCEP.setCellStyle(styleCorpo);

                    Cell cellBairro = row.createCell(cellnum++);
                    cellBairro.setCellValue(endereco.getBairro());
                    cellBairro.setCellStyle(styleCorpo);

                    Cell cellCidade = row.createCell(cellnum++);
                    cellCidade.setCellValue(endereco.getCidade());
                    cellCidade.setCellStyle(styleCorpo);

                    Cell cellEstado = row.createCell(cellnum++);
                    cellEstado.setCellValue(endereco.getEstado());
                    cellEstado.setCellStyle(styleCorpo);

                    Cell cellPais = row.createCell(cellnum++);
                    cellPais.setCellValue(endereco.getPais());
                    cellPais.setCellStyle(styleCorpo);
                }

                try {
                    xssfWorkbook.write(outputStream);
                    outputStream.close();
                    Log.d("EnderecoFragment", "Arquivo Excel criado com sucesso!");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.d("EnderecoFragment", "Arquivo não encontrado!");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("EnderecoFragment", "Erro na edição do arquivo!");
                }
            }
        });
    }

    public void closeFloatingActionMenu() {
        if (famEndereco.isOpened()) {
            famEndereco.close(true);
        }
    }


    public void pesquisar(String pesquisa, String filtro, long limite) {
        if (!pesquisa.equals("")) {
            pesquisa = pesquisa.substring(0, 1).toUpperCase().concat(pesquisa.substring(1));
        }
        filtro = filtro.toLowerCase();

        query = collectionReference.orderBy(filtro).startAt(pesquisa).endAt(pesquisa + "\uf8ff").limit(limite);
        FirestoreRecyclerOptions<Endereco> options = new FirestoreRecyclerOptions.Builder<Endereco>().setQuery(query, Endereco.class).build();
        enderecoAdapter = new EnderecoAdapter(options);

        rcyEnderecos.setHasFixedSize(true);
        rcyEnderecos.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcyEnderecos.setAdapter(enderecoAdapter);

        enderecoAdapter.startListening();

        // getAdapterItemTouch();
        getRecyclerViewClickListener();
    }


    public void getRecyclerViewClickListener() {
        enderecoAdapter.setRCYDocumentSnapshotClickListener(new RCYDocumentSnapshotClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot documentSnapshot, int posicao) {
                Endereco endereco = documentSnapshot.toObject(Endereco.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("endereco", endereco);

                Fragment fragment = new EditEnderecoFragment();
                fragment.setArguments(bundle);

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fmlPesquisa, new OpcoesMenuFragment()).commit();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fmlConteudo, fragment).commit();
                MainActivity.fragment = fragment;

                closeFloatingActionMenu();
            }

            @Override
            public boolean onItemLongClick(DocumentSnapshot documentSnapshot, int posicao) {
                Toast.makeText(getActivity(), "Longo" + documentSnapshot.getId(), Toast.LENGTH_SHORT).show();
                closeFloatingActionMenu();
                return true;
            }
        });
    }

    public void getFabXls() {
        fabXls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFloatingActionMenu();
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.putExtra(Intent.EXTRA_TITLE, "Endereços.xlsx");
                intent.setType("*/*");
                startActivityForResult(intent, REQUEST_CREATE_XLSX);
            }
        });
    }

    public void getFabNovo() {
        fabNovo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFloatingActionMenu();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fmlPesquisa, new OpcoesMenuFragment()).commit();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fmlConteudo, new NovoEnderecoFragment()).commit();
                MainActivity.fragment = new NovoEnderecoFragment();
            }
        });
    }

    public ArrayList<String> setListFiltros() {
        ArrayList<String> listFiltros = new ArrayList<>();
        listFiltros.add("CEP");
        listFiltros.add("Rua");
        listFiltros.add("Bairro");
        listFiltros.add("Cidade");
        listFiltros.add("Estado");

        return listFiltros;
    }
}
