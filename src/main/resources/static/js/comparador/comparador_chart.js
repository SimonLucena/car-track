(() => {
    const graficoCard = document.getElementById('grafico-comparador-card');
    const canvas      = document.getElementById('graficoComparador');
    if (!canvas) return; // página sem gráfico

    let chart = null;

    // Estado local das séries por card (1 ou 2)
    // { label: string, color: string, points: Map<ano, valor> }
    const seriesByCard = new Map();

    const palette = [
        'rgba(75, 192, 192, 1)',
        'rgba(255, 99, 132, 1)',
        'rgba(54, 162, 235, 1)',
        'rgba(255, 159, 64, 1)',
        'rgba(153, 102, 255, 1)',
        'rgba(201, 203, 207, 1)',
    ];

    function ensureChart() {
        if (chart) return chart;
        const ctx = canvas.getContext('2d');
        chart = new Chart(ctx, {
            type: 'line',
            data: { labels: [], datasets: [] },
            options: {
                responsive: true,
                spanGaps: false,
                plugins: { legend: { display: true } },
                scales: { y: { beginAtZero: false } }
            }
        });
        return chart;
    }

    function unionSortedYears() {
        const set = new Set();
        for (const { points } of seriesByCard.values()) {
            for (const year of points.keys()) set.add(year);
        }
        // anos podem vir como string; garanta ordenação crescente numérica
        return Array.from(set).sort((a, b) => parseInt(a) - parseInt(b));
    }

    function rebuildChart() {
        const hasData = seriesByCard.size > 0;

        if (!hasData) {
            if (chart) {
                chart.destroy();
                chart = null;
            }
            graficoCard.style.display = 'none';
            document.getElementById('trend-indicator')?.classList.add('d-none');
            return;
        }

        graficoCard.style.display = 'block';
        const years = unionSortedYears();
        const datasets = [];

        let i = 0;
        for (const { label, color, points } of seriesByCard.values()) {
            const data = years.map(y => points.get(String(y)) ?? null);
            datasets.push({
                label,
                data,
                borderColor: color || palette[i % palette.length],
                backgroundColor: 'transparent',
                fill: false,
                tension: 0.2,
                pointRadius: 3
            });
            i++;
        }

        const inst = ensureChart();
        inst.data.labels = years;
        inst.data.datasets = datasets;
        inst.update();

        atualizarIndicadorTrend(datasets);
    }

    // Atualiza “trend” baseado no último dataset visível (ou em média simples)
    function atualizarIndicadorTrend(datasets) {
        const badge = document.getElementById('trend-indicator');
        if (!badge) return;

        // pegue o último dataset que tenha pelo menos 2 pontos válidos
        let serie = null;
        for (let i = datasets.length - 1; i >= 0; i--) {
            const arr = datasets[i].data.filter(v => v != null);
            if (arr.length >= 2) { serie = arr; break; }
        }
        if (!serie) { badge.className = 'badge rounded-pill fw-normal d-none'; badge.textContent = ''; return; }

        const atual    = serie[serie.length - 1];
        const anterior = serie[serie.length - 2];
        const diff     = atual - anterior;
        const perc     = anterior ? (diff / anterior) * 100 : 0;

        badge.className = 'badge rounded-pill fw-normal';
        badge.classList.remove('bg-success', 'bg-danger', 'bg-secondary');
        badge.innerHTML = '';

        const icone = document.createElement('i');
        icone.classList.add('me-1', 'fas');

        if (diff > 0) { badge.classList.add('bg-success'); icone.classList.add('fa-arrow-up'); }
        else if (diff < 0) { badge.classList.add('bg-danger'); icone.classList.add('fa-arrow-down'); }
        else { badge.classList.add('bg-secondary'); icone.classList.add('fa-minus'); }

        badge.appendChild(icone);
        badge.append(`${diff > 0 ? '+' : ''}${perc.toFixed(1)}%`);
    }

    async function fetchSerie(modeloCodigo) {
        // Endpoint que já existe no seu index.js: /veiculosModelo/{modelo}
        const resp = await fetch('/veiculosModelo/' + encodeURIComponent(modeloCodigo));
        if (!resp.ok) throw new Error('Falha ao buscar série do modelo ' + modeloCodigo);
        const data = await resp.json(); // array de veículos { ano, valor, ... }
        // Map<ano, valor> — se vierem anos repetidos, fica o último
        const points = new Map();
        for (const v of data) {
            points.set(String(v.ano), Number(v.valor));
        }
        return points;
    }

    async function addOrUpdateCard(card, modeloCodigo, modeloNome) {
        const color = palette[(card - 1) % palette.length];
        const points = await fetchSerie(modeloCodigo);
        seriesByCard.set(String(card), { label: modeloNome, color, points });
        rebuildChart();
    }

    function removeCard(card) {
        seriesByCard.delete(String(card));
        rebuildChart();
    }

    // ===== Boot: reconstrói o gráfico conforme o que já está renderizado na página =====
    document.addEventListener('DOMContentLoaded', async () => {
        const seeds = document.querySelectorAll('[data-chart-seed]');
        if (seeds.length === 0) { rebuildChart(); return; }

        // Carrega em paralelo as séries visíveis (card 1 e/ou 2)
        await Promise.all(Array.from(seeds).map(async seed => {
            const card = Number(seed.dataset.card);
            const modeloCodigo = seed.dataset.modeloCodigo;
            const modeloNome   = seed.dataset.modeloNome;
            await addOrUpdateCard(card, modeloCodigo, modeloNome);
        }));
    });

    // (Opcional) Se quiser reagir sem reload quando clicar nos links "Limpar",
    // você pode interceptar aqui e chamar removeCard(card) manualmente,
    // mas como sua navegação faz full reload, o rebuild acima já resolve.

})();
