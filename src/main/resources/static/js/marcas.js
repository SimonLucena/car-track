document.addEventListener('DOMContentLoaded', () => {
    fetch('/api/medias-preco-marcas')
        .then(r => r.json())
        .then(data => {
            /* data = { "Fiat": 75000.12, "Volkswagen": 89000.45, ... } */
            const labels = Object.keys(data);
            const valores = Object.values(data);

            const ctx = document.getElementById('graficoMediaMarcas').getContext('2d');
            new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Preço médio (R$)',
                        data: valores,
                        backgroundColor: 'rgba(0, 123, 255, 0.6)',
                        borderColor:   'rgba(0, 123, 255, 1)',
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    plugins: {
                        tooltip: {
                            callbacks: {
                                label: ctx => ctx.parsed.y.toLocaleString('pt-BR', {
                                    style: 'currency', currency: 'BRL'
                                })
                            }
                        }
                    },
                    scales: {
                        y: { beginAtZero: true }
                    }
                }
            });
        })
        .catch(err => console.error('Erro ao buscar médias:', err));
});
