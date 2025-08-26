document.getElementById('marca2').addEventListener('change', function () {
    const marcaCodigo = this.value;
    const modeloSelect = document.getElementById('modelo2');

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
            console.error('Erro ao buscar modelo2:', error);
            modeloSelect.disabled = true;
        });
});

document.getElementById('modelo2').addEventListener('change', function () {
    const modeloCodigo = this.value;
    const anoSelect = document.getElementById("ano2");
    const botaoSubmit = document.getElementById("submeter_pesquisa2");

    botaoSubmit.disabled = modeloCodigo === '0';

    fetch('/anosModelo/' + modeloCodigo)
        .then(response => response.json())
        .then(data => {
            anoSelect.innerHTML = '<option selected value="0">-- Selecione um ano --</option>';
            data.forEach(ano => {
                const opt = document.createElement('option');
                opt.value = ano;
                opt.textContent = ano;
                anoSelect.appendChild(opt);
            })
            anoSelect.disabled = false;
        })
        .catch(error => {
            console.log("erro ao buscar o ano:", error);
            anoSelect.disabled = true;
        })
});