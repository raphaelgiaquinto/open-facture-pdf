async function generate () {
    const response = await fetch('/import', {
        method: 'POST',
        body: {
            //invoices
        }
    })
    const status = response.status
    if (status === 400) {
        errors.innerText = await response.text()
    } else if (status === 200) {
        const blob = await response.blob()
        const url = window.URL.createObjectURL(blob)
        dlLink.href = url
        dlLink.download = 'factures.zip'
        dlLink.click()
        window.URL.revokeObjectURL(url)
        success.innerText = 'Succès ! Le téléchargement de vos factures va débuter.'
    } else {
        errors.innerText = 'Une erreur est survenue, vérifiez le fichier et re-essayez.'
    }
}