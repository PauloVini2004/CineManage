package br.ufrpe.cine_rural.util;

import br.ufrpe.cine_rural.model.Cliente;
import br.ufrpe.cine_rural.model.Ingresso;
import br.ufrpe.cine_rural.model.VendaIngresso;
import br.ufrpe.cine_rural.model.loja.ItemVenda;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GeradorPDF {



        public static void gerarNotaFiscalProduto(
                Cliente cliente,
                List<ItemVenda> itens,
                double total
        ) {

            try {

                String pasta = "cinemanagerfx/src/main/java/br/ufrpe/cine_rural/dados/arquivospdf/";

                new File(pasta).mkdirs();

                String nomeArquivo = pasta
                                +"NotaFiscal_"
                                + System.currentTimeMillis()
                                + ".pdf";

                System.out.println("PDF EM: " + nomeArquivo);

                PdfWriter writer =
                        new PdfWriter(nomeArquivo);

                PdfDocument pdf =
                        new PdfDocument(writer);

                Document document =
                        new Document(pdf);

                document.add(
                        new Paragraph(
                                "CINE RURAL"
                        )
                );

                document.add(
                        new Paragraph(
                                "NOTA FISCAL DE VENDA"
                        )
                );

                document.add(
                        new Paragraph(
                                "Data: "
                                        + LocalDateTime.now()
                                        .format(
                                                DateTimeFormatter.ofPattern(
                                                        "dd/MM/yyyy HH:mm"
                                                )
                                        )
                        )
                );

                document.add(
                        new Paragraph(
                                "Cliente: "
                                        + cliente.getNome()
                        )
                );

                document.add(
                        new Paragraph(
                                "CPF: "
                                        + cliente.getCpf()
                        )
                );

                document.add(
                        new Paragraph(" ")
                );

                Table tabela = new Table(
                        new float[]{4, 1, 2, 2}
                );

                tabela.addCell("Produto");
                tabela.addCell("Qtd");
                tabela.addCell("Preço");
                tabela.addCell("Subtotal");

                for (ItemVenda item : itens) {

                    tabela.addCell(
                            item.getProduto().getNome()
                    );

                    tabela.addCell(
                            String.valueOf(
                                    item.getQuantidade()
                            )
                    );

                    tabela.addCell(
                            String.format(
                                    "R$ %.2f",
                                    item.getProduto().getPreco()
                            )
                    );

                    tabela.addCell(
                            String.format(
                                    "R$ %.2f",
                                    item.getSubtotal()
                            )
                    );
                }

                document.add(tabela);

                document.add(
                        new Paragraph(
                                "\nTOTAL: R$ "
                                        + String.format(
                                        "%.2f",
                                        total
                                )
                        )
                );

                document.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    public static void gerarNotaFiscalIngresso(VendaIngresso venda) {

        try {

            String pasta = "cinemanagerfx/src/main/java/br/ufrpe/cine_rural/dados/arquivospdf/";

            new File(pasta).mkdirs();

            String nomeArquivo = pasta
                            +"NotaFiscalIngresso_"
                            + System.currentTimeMillis()
                            + ".pdf";

            System.out.println("PDF EM: " + nomeArquivo);

            PdfWriter writer =
                    new PdfWriter(nomeArquivo);

            PdfDocument pdf =
                    new PdfDocument(writer);

            Document document =
                    new Document(pdf);

            document.add(
                    new Paragraph("CINE RURAL")
            );

            document.add(
                    new Paragraph("NOTA FISCAL DE VENDA DE INGRESSOS")
            );

            document.add(
                    new Paragraph(
                            "Data: "
                                    + venda.getDataVenda()
                                    .format(
                                            DateTimeFormatter.ofPattern(
                                                    "dd/MM/yyyy HH:mm"
                                            )
                                    )
                    )
            );

            document.add(
                    new Paragraph(
                            "Forma de pagamento: "
                                    + venda.getFormaPagamento()
                    )
            );

            document.add(
                    new Paragraph(" ")
            );

            if (!venda.getIngressos().isEmpty()
                    && venda.getIngressos().get(0).getCliente() != null) {

                Cliente cliente =
                        venda.getIngressos()
                                .get(0)
                                .getCliente();

                document.add(
                        new Paragraph(
                                "Cliente: "
                                        + cliente.getNome()
                        )
                );

                document.add(
                        new Paragraph(
                                "CPF: "
                                        + cliente.getCpf()
                        )
                );
            }

            document.add(
                    new Paragraph(" ")
            );

            Table tabela = new Table(
                    new float[]{4, 2, 2, 2, 1}
            );

            tabela.addCell("Filme");
            tabela.addCell("Assento");
            tabela.addCell("Categoria");
            tabela.addCell("Preço");
            tabela.addCell("Idade");

            double total = 0;

            for (Ingresso ingresso : venda.getIngressos()) {

                tabela.addCell(
                        ingresso.getSessao()
                                .getFilme()
                                .getTitulo()
                );

                tabela.addCell(
                        ingresso.getAssento()
                                .getCodigo()
                );

                tabela.addCell(
                        ingresso.getCategoria()
                                .toString()
                );

                tabela.addCell(
                        String.format(
                                "R$ %.2f",
                                ingresso.getPreco()
                        )
                );

                String idadeStr = (ingresso.getCliente() != null)
                        ? String.valueOf(ingresso.getCliente().getIdade())
                        : "-";
                tabela.addCell(idadeStr);

                total += ingresso.getPreco();
            }

            document.add(tabela);

            document.add(
                    new Paragraph(
                            "\nTOTAL: R$ "
                                    + String.format(
                                    "%.2f",
                                    total
                            )
                    )
            );

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public static void gerarIngresso(Ingresso ingresso) {

        try {

            String pasta = "cinemanagerfx/src/main/java/br/ufrpe/cine_rural/dados/arquivospdf/";

            new File(pasta).mkdirs();

            String nomeArquivo =
                            pasta
                            +"Ingresso_"
                            + ingresso.getAssento().getCodigo()
                            + "_"
                            + System.currentTimeMillis()
                            + ".pdf";

            System.out.println("PDF EM: " + nomeArquivo);

            PdfWriter writer =
                    new PdfWriter(nomeArquivo);

            PdfDocument pdf =
                    new PdfDocument(writer);

            Document document =
                    new Document(pdf);

            document.add(
                    new Paragraph("CINE RURAL")
            );

            document.add(
                    new Paragraph("INGRESSO")
            );

            document.add(
                    new Paragraph("--------------------------------")
            );

            document.add(
                    new Paragraph(
                            "Filme: "
                                    + ingresso.getSessao()
                                    .getFilme()
                                    .getTitulo()
                    )
            );

            document.add(
                    new Paragraph(
                            "Sala: "
                                    + ingresso.getSessao()
                                    .getSala()
                    )
            );

            document.add(
                    new Paragraph(
                            "Horário: "
                                    + ingresso.getSessao()
                                    .getHorario()
                    )
            );

            document.add(
                    new Paragraph(
                            "Assento: "
                                    + ingresso.getAssento()
                                    .getCodigo()
                    )
            );

            document.add(
                    new Paragraph(
                            "Categoria: "
                                    + ingresso.getCategoria()
                    )
            );

            document.add(
                    new Paragraph(
                            "Valor Pago: R$ "
                                    + String.format(
                                    "%.2f",
                                    ingresso.getPreco()
                            )
                    )
            );

            if (ingresso.getCliente() != null) {

                document.add(
                        new Paragraph(
                                "Cliente: "
                                        + ingresso.getCliente().getNome()
                        )
                );

                document.add(
                        new Paragraph(
                                "Idade: "
                                        + ingresso.getCliente().getIdade()
                                        + " anos"
                        )
                );
            }

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
