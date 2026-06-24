export function validateInstitutionalEmail(email: string): boolean {
  const normalizedEmail = email.toLowerCase().trim();

  const allowedDomains = [
    "edu",
    "edu.br",
    "cefet-rj.br",
    "aluno.cefet-rj.br",
    "estudante.cefet-rj.br",
  ];

  return allowedDomains.some((domain) =>
    normalizedEmail.endsWith(`@${domain}`) ||
    normalizedEmail.endsWith(`.${domain}`)
  );
}