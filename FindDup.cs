using System;
using System.IO;
using System.Text.RegularExpressions;

class Program {
    static void Main() {
        foreach (var file in Directory.GetFiles(".", "*.java", SearchOption.AllDirectories)) {
            var text = File.ReadAllText(file);
            // find if @RolesAllowed appears twice without a class/method declaration between them
            // a declaration usually contains '{'
            var parts = text.Split('{');
            foreach (var part in parts) {
                var matches = Regex.Matches(part, "@RolesAllowed");
                if (matches.Count > 1) {
                    Console.WriteLine("FOUND DUPLICATE IN: " + file);
                }
            }
        }
    }
}