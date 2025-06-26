document.getElementById('marcas').addEventListener('change', function () {
    const marcaCodigo = this.value;
    const modeloSelect = document.getElementById('modelos');

    if (marcaCodigo === "0") {
        modeloSelect.innerHTML = '<option selected value="0">-- Selecione um modelo --</option>';
        modeloSelect.disabled = true;
        return;
    }

    fetch('/modelos/' + marcaCodigo)
        .then(response => response.json())
        .then(data => {
            modeloSelect.innerHTML = '<option selected value="0">-- Selecione um modelo --</option>';
            data.forEach(modelo => {
                const option = document.createElement('option');
                option.value = modelo.codigo;
                option.textContent = modelo.nome;
                modeloSelect.appendChild(option);
            });
            modeloSelect.disabled = false;
        })
        .catch(error => {
            console.error('Erro ao buscar modelos:', error);
            modeloSelect.disabled = true;
        });
});

let chartInstance = null;

function gerarTabelaFiltrada() {
    const marcaSelect = document.getElementById('marcas');
    const modeloSelect = document.getElementById('modelos');
    const tableCard = document.getElementById('tabela-veiculos-card');
    const tableBody = document.getElementById('tabela-veiculos-content');
    const graficoCard = document.getElementById('grafico-veiculos-card');
    const canvas = document.getElementById('graficoAnoPreco');

    if (marcaSelect.value === "0" || modeloSelect.value === "0") {
        tableCard.style.display = "none";
        graficoCard.style.display = "none";
        return;
    }

    fetch('/veiculosModelo/' + modeloSelect.value)
        .then(response => response.json())
        .then(data => {
            tableBody.innerHTML = "";
            const anos = [];
            const precos = [];

            data.forEach(veiculo => {
                const tr = document.createElement('tr');

                // Formata valor para moeda brasileira
                const valorFormatado = veiculo.valor.toLocaleString('pt-BR', {
                    style: 'currency',
                    currency: 'BRL'
                });

                tr.innerHTML = `
                        <td>${veiculo.codigoFipe}</td>
                        <td>${veiculo.modelo.nome} - ${veiculo.ano}</td>
                        <td>${valorFormatado}</td>
                        <td>${veiculo.combustivel}</td>
                    `;
                tableBody.appendChild(tr);

                anos.push(veiculo.ano);
                precos.push(veiculo.valor);
            });
            atualizarIndicadorTrend(precos.slice().reverse());

            tableCard.style.display = "block";
            graficoCard.style.display = "block";

            if (chartInstance) {
                chartInstance.destroy();
            }

            const ctx = canvas.getContext('2d');
            chartInstance = new Chart(ctx, {
                type: 'line',
                data: {
                    labels: anos,
                    datasets: [{
                        label: 'Preço (R$)',
                        data: precos,
                        fill: false,
                        borderColor: 'rgba(75, 192, 192, 1)',
                        backgroundColor: 'rgba(75, 192, 192, 0.2)',
                        tension: 0.2
                    }]
                },
                options: {
                    responsive: true,
                    scales: {
                        y: {
                            beginAtZero: false
                        }
                    }
                }
            });
        })
        .catch(error => {
            console.error('Erro ao buscar veículos:', error);
            tableCard.style.display = "none";
            graficoCard.style.display = "none";
        });
}

function atualizarIndicadorTrend(valoresReverso) {
    if (valoresReverso.length < 2) return;

    const atual     = valoresReverso[valoresReverso.length - 2];
    const anterior  = valoresReverso[valoresReverso.length - 1];
    const diff      = atual - anterior;
    const perc      = (diff / anterior) * 100;

    const badge = document.getElementById('trend-indicator');
    badge.classList.remove('bg-success', 'bg-danger', 'bg-secondary');
    badge.innerHTML = '';                            // limpa ícone+texto

    const icone = document.createElement('i');
    icone.classList.add('me-1', 'fas');

    if (diff > 0) {
        badge.classList.add('bg-success');
        icone.classList.add('fa-arrow-up');
    } else if (diff < 0) {
        badge.classList.add('bg-danger');
        icone.classList.add('fa-arrow-down');
    } else {
        badge.classList.add('bg-secondary');
        icone.classList.add('fa-minus');
    }

    badge.appendChild(icone);
    badge.append(
        `${diff > 0 ? '+' : ''}${perc.toFixed(1)}%`
    );
}
